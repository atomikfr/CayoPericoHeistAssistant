package jmodmenu.cayo_perico.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
	
	private  Map<LootType, Integer> scopeIslandData = new HashMap<>();
	private  Map<LootType, Integer> scopeCompoundData = new HashMap<>();
	private  Map<LootType, Boolean> requestScoping = new HashMap<>();
	
	boolean lootChanged = false;
	boolean scopeChanged = false;
	
	public void reload() {
		secondaryIslandLoot = new LinkedList<>( service.getIslandLoot(playerIndex) );
		secondaryCompundLoot = new LinkedList<>( service.getCompoundLoot(playerIndex) );
		for (LootType type : LootType.values()) {
			scopeIslandData.put(type, service.getScopeData(playerIndex, type, true));
			scopeCompoundData.put(type, service.getScopeData(playerIndex, type, false));
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
		AtomicInteger mask = new AtomicInteger();
		loots.stream()
			.filter( loot -> loot.getType() == type )
			.forEach( loot -> mask.set(mask.get() | (1 << loot.getIdx())) );
		return mask.get();
	}
	
	public boolean scoped(LootType type) {
		  return (generateScopeMask(type, true) == scopeIslandData.get(type))
		   && (generateScopeMask(type, false) == scopeCompoundData.get(type));
	}
	
	public void unsetLootAtPosition(int idx, boolean island) {
		List<? extends SecondaryLoot> loots = island ? secondaryIslandLoot : secondaryCompundLoot;
		
		SecondaryLoot found = loots.stream()
				.filter( loot -> loot.getIdx() == idx )
				.findFirst()
				.orElse(null);
		if ( found != null ) {
			loots.remove(found);
			lootChanged = true;
			requestScoping(found.getType(), true);
		}
	}
	
	public void unsetPaintAtPosition(int idx) {
		SecondaryLoot found = secondaryCompundLoot.stream()
				.filter( loot -> loot.getIdx() == idx )
				.filter( loot -> loot.getType() == LootType.PAINTINGS )
				.findFirst()
				.orElse(null);
		if ( found != null ) {
			secondaryCompundLoot.remove(found);
			lootChanged = true;
			requestScoping(LootType.PAINTINGS, true);
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
		requestScoping(type, true);
	}
	
	public void requestScoping(LootType type, boolean scope) {
		scopeChanged = true;
		requestScoping.put(type, scope);
	}

	public void saveChanges() {
		
		for(LootType type: LootType.values()) {
			if ( type != LootType.PAINTINGS ) {
				int mask = generateScopeMask(type, true);
				if ( lootChanged ) {
					log.debug( String.format("Island loot %s mask[%08x]", type.name(), mask) );
					service.setLootPosition(type, true, mask);
				}
				if ( scopeChanged ) {
					int scopingMask = requestScoping.getOrDefault(type, false) ? mask : 0;
					service.setLootScope(type, true, scopingMask);
				}
			}
			int mask = generateScopeMask(type, false);
			if ( lootChanged ) {
				log.debug(  String.format("Compound loot %s mask[%08x]", type.name(), mask) );
				service.setLootPosition(type, false, mask);
			}
			if ( scopeChanged ) {
				int scopingMask = requestScoping.getOrDefault(type, false) ? mask : 0;
				service.setLootScope(type, false, scopingMask);
			}
		}
	}

	public boolean hasRequestedScope(LootType type) {
		return requestScoping.getOrDefault(type, false);
	}

}
