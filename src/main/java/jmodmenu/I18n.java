package jmodmenu;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Optional;

import org.yaml.snakeyaml.Yaml;

public class I18n {
	
	private static I18n instance;
	private String loadedLang;
	
	@SuppressWarnings("unchecked")
	public static void load(String lang) {
		boolean loaded = Optional.ofNullable(instance)
			.map( i -> i.loadedLang )
			.filter( actual -> actual.equals(lang) )
			.isPresent();
		if (loaded) return;
		
		URL url = Optional.ofNullable( I18n.class.getClassLoader().getResource("lang/"+lang+".yml") )
				.orElse( I18n.class.getClassLoader().getResource("lang/en.yml") );
		instance = new I18n();
		try ( InputStream in = url.openStream() ) {
			instance.textMap = new Yaml().loadAs(in, Map.class);
			instance.loadedLang = lang;
		} catch (IOException e) {
			throw new RuntimeException("Unable to load lang ["+lang+"]", e);
		}
	}
	
	private Map<String, Map<String, String>> textMap;
	
	public static String txt(String key) {
		if ( key.indexOf('.') < 0 ) 
			throw new InvalidParameterException("translation key ["+key+"] should contains .");
		
		
		String[] keys = key.split("\\.");
		Map<String, String> values = Optional.ofNullable( instance.textMap.get(keys[0]) )
			.orElseThrow( () -> new InvalidParameterException("translation map ["+keys[0]+"] not found.") );
		
		return values.getOrDefault(keys[1], keys[1]);
	}

}
