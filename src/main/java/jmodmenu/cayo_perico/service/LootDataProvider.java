package jmodmenu.cayo_perico.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;

import jmodmenu.cayo_perico.model.LootType;
import jmodmenu.cayo_perico.model.SecondaryCompundLoot;
import jmodmenu.cayo_perico.model.SecondaryIslandLoot;
import jmodmenu.cayo_perico.model.SecondaryLoot;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LootDataProvider {
	
	@NonNull
	CayoPericoMapService service;
	@NonNull
	Integer playerIndex;
	
	@Getter
	List<SecondaryIslandLoot> secondaryIslandLoot;
	@Getter
	List<SecondaryCompundLoot> secondaryCompundLoot;
	
	private  int scopeIslandData;
	private  int scopeCompoundData;
	private  Map<LootType, Boolean> requestScoping = new HashMap<>();
	
	boolean lootChanged = false;
	boolean scopeChanged = false;
	
	public void reload() {
		secondaryIslandLoot = new LinkedList<>( service.getIslandLoot(playerIndex) );
		secondaryCompundLoot = new LinkedList<>( service.getCompoundLoot(playerIndex) );
		for (LootType type : LootType.values()) {
			scopeIslandData |= service.getScopeData(playerIndex, type, true);
			scopeCompoundData |= service.getScopeData(playerIndex, type, false);
			
			requestScoping.put(type, service.hasScopedLoot(playerIndex, type) );
		}
	}
	
	public List<SecondaryLoot> allSecondaryLoots() {
		LinkedList<SecondaryLoot> res = new LinkedList<>();
		res.addAll(secondaryIslandLoot);
		res.addAll(secondaryCompundLoot);
		return res;
	}
	
	private int generateScopeMask(LootType type, boolean island) {
		List<? extends SecondaryLoot> loots = island ? secondaryIslandLoot : secondaryCompundLoot;
		return loots.stream()
			.filter( loot -> loot.getType() == type )
			// .forEach( loot -> mask.set(mask.get() | (1 << loot.getId())) );
			.collect(Collector.<SecondaryLoot, Integer>of(
				() -> 0,
				(Integer acc, SecondaryLoot item) -> acc += (1 << item.getId()),
				(a,b) -> a + b,
				Characteristics.UNORDERED
			));
	}
	
	public boolean scoped(LootType type) {
		int expectedScope = generateScopeMask(type, true);
		if ( (expectedScope & scopeIslandData) != expectedScope ) return false;
		expectedScope = generateScopeMask(type, false);
		if ( (expectedScope & scopeCompoundData) != expectedScope ) return false;
		return true;
	}
	
	public void unsetLootAtPosition(int idx, boolean island) {
		List<? extends SecondaryLoot> loots = island ? secondaryIslandLoot : secondaryCompundLoot;
		
		SecondaryLoot found = loots.stream()
				.filter( loot -> loot.getId() == idx )
				.findFirst()
				.orElse(null);
		if ( found != null ) {
			loots.remove(found);
			lootChanged = true;
			// requestScoping(found.getType(), true);
		}
	}
	
	public void unsetPaintAtPosition(int idx) {
		SecondaryLoot found = secondaryCompundLoot.stream()
				.filter( loot -> loot.getId() == idx )
				.filter( loot -> loot.getType() == LootType.PAINTINGS )
				.findFirst()
				.orElse(null);
		if ( found != null ) {
			secondaryCompundLoot.remove(found);
			lootChanged = true;
			// requestScoping(LootType.PAINTINGS, true);
		}
	}
	
	public void addLoot(LootType type, int idx, boolean island) {
		if ( island ) {
			SecondaryIslandLoot loot = new SecondaryIslandLoot(type, idx);
			secondaryIslandLoot.add(loot);
		} else {
			SecondaryCompundLoot loot = new SecondaryCompundLoot(type, idx);
			secondaryCompundLoot.add(loot);
		}
		lootChanged = true;
		// requestScoping(type, true);
	}
	
	public void requestScoping(LootType type, boolean scope) {
		scopeChanged = true;
		requestScoping.put(type, scope);
	}

	public void saveChanges() {
		Set<LootType> requestedScopeTypes = requestScoping.keySet()
			.stream()
			.filter(requestScoping::get)
			.collect(Collectors.toSet());
		saveIfChanges(service, true, requestedScopeTypes, secondaryIslandLoot);
		saveIfChanges(service, false, requestedScopeTypes, secondaryCompundLoot);
	}
	
	private static void saveIfChanges(CayoPericoMapService service, boolean onIsland, Set<LootType> requestScoping, List<? extends SecondaryLoot> loots) {
		int playerIndex = service.getLocalPlayerIndex();
		Map<LootType, Integer> original = 
				onIsland ? SecondaryLoot.positionValues(service.getIslandLoot(playerIndex)) : SecondaryLoot.positionValues(service.getCompoundLoot(playerIndex)) ;
		Map<LootType, Integer> afterMenu = SecondaryLoot.positionValues( loots );
		String debugLabel = onIsland ? "Island" : "Compound";
		for(LootType type: LootType.values()) {
			if ( onIsland && type == LootType.PAINTINGS ) continue;
			int originalPosition = original.get(type);
			int newPosition = afterMenu.get(type);
			if ( originalPosition != newPosition ) {
				log.debug( String.format("CHANGED %s loot %s oldValue[%d] set value => hex[%08x] dec[%d]", debugLabel, type.name(), originalPosition, newPosition, newPosition) );
				service.setLootPosition(type, onIsland, newPosition);
			}
			int oldScope = service.getScopeData(playerIndex, type, onIsland);
			int newScope = requestScoping.contains(type) ? newPosition : (oldScope & newPosition);
			if ( oldScope != newScope ) {
				log.debug( String.format("CHANGED %s scope %s oldValue[%d] set value => hex[%08x] dec[%d]", debugLabel, type.name(), oldScope, newScope, newScope) );
				service.setLootScope(type, onIsland, newPosition);
			}
		}
	}

	public boolean hasRequestedScope(LootType type) {
		return requestScoping.getOrDefault(type, false);
	}

}
