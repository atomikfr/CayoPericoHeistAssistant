package jmodmenu.cayo_perico.ui;

import java.awt.Point;

public class CalibrationReference {

	float mapX, mapY;
	int x, y;
	float xfactor, yfactor, xoffset, yoffset;

	CalibrationReference previous = null;
	
	CalibrationReference to(int x, int y) {
		this.x = x;
		this.y = y;
		CalibrationReference ref = this;
		
		if ( previous != null ) {
			xfactor = (x - previous.x) / (mapX - previous.mapX);
			xoffset = (int) (x - xfactor * mapX);
			yfactor = (y - previous.y) / (mapY - previous.mapY);
			yoffset = (int) (y - yfactor * mapY);
			
			previous.mapX = mapX;
			previous.mapY = mapY;
			previous.x = x;
			previous.y = y;
		} else {
			ref = new CalibrationReference();			
			ref.previous = this;
		}
		return ref;
	}
	CalibrationReference calibrate(float mapX, float mapY) {
		this.mapX = mapX;
		this.mapY = mapY;
		
		return this;
	}
	Point apply(float x, float y) {
		Point p = new Point();
		p.x = (int) ((x * xfactor) + xoffset);
		p.y = (int) ((y * yfactor) + yoffset);
		return p;
	}
}