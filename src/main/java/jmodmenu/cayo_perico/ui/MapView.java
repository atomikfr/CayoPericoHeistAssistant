package jmodmenu.cayo_perico.ui;

enum MapView {
	ISLAND("cayo_perico_map.png", 
		new CalibrationReference()
		.calibrate(4052.4f, -4655.79f).to(193, 217)
		.calibrate(5478.63f, -5847.74f).to(814, 740)
	),
	COMPOUND("cayo_perico_base.png", 
		new CalibrationReference()
		.calibrate(4995.553f, -5748.032f).to(372, 459)
		.calibrate(5081.14f, -5758.794f).to(796, 608)
	);
	
	String imageFile;
	CalibrationReference calibrationReference;
	
	MapView(String imageFile, CalibrationReference calibrationReference) {
		this.imageFile = imageFile;
		this.calibrationReference = calibrationReference;
	}
}