package jmodmenu.cayo_perico.ui;

import static jmodmenu.I18n.txt;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuCuts extends MenuAbstract {

	public MenuCuts(MenuContext context) {
		super(context);
	}
	
	List<Field> fields = new LinkedList<Field>();

	@Override
	void content(MenuManager menuManager) {
		menuManager
		.addFields(service.getCuts(), fields)
		.addAction(txt("cuts.all_85"), () -> fields.stream()
			.forEach( f -> f.set("85"))
		);
	}
	
	@Override
	protected void save() {
		Integer[] values = fields.stream()
				.map( Field::intValue )
				.collect(Collectors.toList())
				.toArray(new Integer[] {});
		service.setCuts(values);
	}

	
}
