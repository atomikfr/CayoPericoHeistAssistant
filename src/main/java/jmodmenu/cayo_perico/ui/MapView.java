package jmodmenu.cayo_perico.ui;

import java.awt.Rectangle;

enum MapView {
	ISLAND("cayo_perico_map.png", 
		new CalibrationReference()
		.calibrate(4052.4f, -4655.79f).to(193, 217)
		.calibrate(5478.63f, -5847.74f).to(814, 740),
		null, null
	),
	COMPOUND("zoom_compound.PNG", 
		new CalibrationReference()
		.calibrate(5029.346f, -5733.933f).to(582, 413)
		.calibrate(5082.945f, -5758.447f).to(885, 555),
		new Rectangle(587, 668, 60, 55),
		new Rectangle(33, 27, 200, 207)
	),
	NORTH_DOCK("zoom_north_dock.PNG",
		new CalibrationReference()		
		.calibrate(5060.167f, -4589.7f).to(405, 316)
		.calibrate(5170.703f, -4675.337f).to(823, 640),
		new Rectangle(590, 153, 150, 150),
		new Rectangle(33, 27, 200, 207)
	),
	MAIN_DOCK("zoom_main_dock.PNG",
		new CalibrationReference()
		.calibrate(5001.3f, -5165.434f).to(808, 506)
		.calibrate(4924.385f, -5245.882f).to(509, 814),
		new Rectangle(499, 368, 150, 150),
		new Rectangle(33, 27, 200, 207)
	),
	AIRSTRIP("zoom_airstrip.PNG",
		new CalibrationReference()
		.calibrate(4435.688f, -4446.595f).to(423, 104)
		.calibrate(4503.678f, -4556.323f).to(657, 480),
		new Rectangle(319, 100, 150, 150),
		new Rectangle(33, 27, 200, 207)
	),
	FIELDS("zoom_fields.PNG",
		new CalibrationReference()
		.calibrate(5196.664f, -5133.933f).to(468, 217)
		.calibrate(5329.472f, -5272.372f).to(956, 724),
		new Rectangle(636, 364, 150, 150),
		new Rectangle(33, 27, 200, 207)
	)
	;
	
	String imageFile;
	CalibrationReference calibrationReference;
	
	Rectangle zoomIn;
	Rectangle zoomOut;
	
	
	MapView(String imageFile, CalibrationReference calibrationReference, Rectangle zoomIn, Rectangle zoomOut) {
		this.imageFile = imageFile;
		this.calibrationReference = calibrationReference;
		this.zoomIn = zoomIn;
		this.zoomOut = zoomOut;
	}
}