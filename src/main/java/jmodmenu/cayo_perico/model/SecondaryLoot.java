package jmodmenu.cayo_perico.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SecondaryLoot implements MapItem {
	
	@Getter
	int id;
	LootType type;
	
	SecondaryLoot(LootType type, int idx) {
		this.type = type;
		this.id = idx;
	}
	
	@Override
	public String name() {
		return type.name();
	}
	
	public static <T extends SecondaryLoot> Map<LootType, Long> countByType(List<T> list) {
		return list.stream()
			.collect( Collectors.groupingBy(SecondaryLoot::getType, Collectors.counting()) );
	}
	
	public static <T extends SecondaryLoot> Map<LootType, Integer> positionValues(List<T> items) {
		int[] positions = new int[ LootType.values().length ];
		items.stream()
			.forEach( item -> positions[item.getType().ordinal()] |= (1 << item.getId()) );
		Map<LootType, Integer> result = new HashMap<>();
		for( LootType type : LootType.values() ) {
			result.put(type, positions[type.ordinal()]);
		};
		return result;
	}
	

}
