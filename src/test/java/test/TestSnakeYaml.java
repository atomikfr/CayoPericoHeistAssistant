package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.InvalidParameterException;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import jmodmenu.I18n;

public class TestSnakeYaml {
	
	@Test
	@SuppressWarnings("unchecked")
	public void testLoadFile() {
		Yaml yaml = new Yaml();
		Map<String, Object> map = yaml.loadAs( getClass().getClassLoader().getResourceAsStream("testfile.yml"), Map.class );
		Object o = map.get("hello");
		assertTrue( o instanceof Map );
		assertTrue( o.toString().contains("somedetails") );
	}
	
	@Test
	public void testLoadI18n() {
		I18n.load("fr");
		String text = I18n.txt("menu.scope_out");
		assertEquals("Repérage", text);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testNoKey() {
		I18n.load("fr");
		I18n.txt("toto.scope_out");
	}
	
	@Test
	public void testDefaultValue() {
		I18n.load("fr");
		String text = I18n.txt("menu.no_such_menu");
		assertEquals("no_such_menu", text);
	}
	
	@Test
	public void testDefaultLang() {
		String lang = System.getProperty("user.language");
		assertEquals("fr", lang);
	}

}
