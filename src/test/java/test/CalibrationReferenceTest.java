package test;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import jmodmenu.cayo_perico.ui.CalibrationReference;
import jmodmenu.cayo_perico.ui.MapIcon;

public class CalibrationReferenceTest {

	CalibrationReference ref;
	
	@Before
	public void setup() {
		ref = new CalibrationReference()
		.calibrate(4052.4f, -4655.79f).to(193, 217)
		.calibrate(5478.63f, -5847.74f).to(814, 740);
	}
	
	@Test
	public void testCalibration() {
		assertEquals(0.44, ref.getXfactor(), 0.01);
		assertEquals(-0.44, ref.getYfactor(), 0.01);
	}
	
	@Test
	public void testPointMapIcon() {
		MapIcon icon = new MapIcon.Builder()
				.pos(4447.822f, -4442.135f, 7.175f)
				.build();
		Point p = icon.apply(ref);
		assertEquals(365, p.x);
		assertEquals(124, p.y);
	}
	
	
}
