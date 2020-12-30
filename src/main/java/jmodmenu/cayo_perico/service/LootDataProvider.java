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
	
	boolean changed = false;
	
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
			changed = true;
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
			changed = true;
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
		changed = true;
	}

	public void saveChanges() {
		if ( !changed ) {
			log.info("No changes made on loots");
			return;
		}
		
		for(LootType type: LootType.values()) {
			if ( type != LootType.PAINTINGS ) {
				int mask = generateScopeMask(type, true);
				log.debug( String.format("Island loot %s mask[%08x]", type.name(), mask) );
				service.setLootPosition(type, true, mask);
				service.setLootScope(type, true, mask);
			}
			int mask = generateScopeMask(type, false);
			log.debug(  String.format("Compound loot %s mask[%08x]", type.name(), mask) );
			service.setLootPosition(type, false, mask);
			service.setLootScope(type, false, mask);
		}
	}

}
