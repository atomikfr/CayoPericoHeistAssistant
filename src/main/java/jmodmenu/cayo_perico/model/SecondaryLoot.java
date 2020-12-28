package jmodmenu.cayo_perico.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SecondaryLoot implements MapItem {
	
	int idx;
	LootType type;
	
	SecondaryLoot(LootType type, int idx) {
		this.type = type;
		this.idx = idx;
	}
	
	@Override
	public String name() {
		return type.name();
	}
	
	public static <T extends SecondaryLoot> Map<LootType, Long> countByType(List<T> list) {
		return list.stream()
			.collect( Collectors.groupingBy(SecondaryLoot::getType, Collectors.counting()) );
	}
	

}
