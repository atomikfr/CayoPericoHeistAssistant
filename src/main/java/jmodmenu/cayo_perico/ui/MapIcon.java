package jmodmenu.cayo_perico.ui;

import java.awt.Color;
import java.awt.Point;

public class MapIcon {
	float[] pos = new float[3];
	Color color = Color.WHITE;
	int xoffset, yoffset;
	
	public static class Builder {
		MapIcon mapIcon = new MapIcon();
		public Builder pos(float x, float y, float z) {
			mapIcon.pos[0] = x;
			mapIcon.pos[1] = y;
			mapIcon.pos[2] = z;
			return this;
		}
		public Builder color(Color color) {
			mapIcon.color = color;
			return this;
		}
		public MapIcon build() {
			return mapIcon;
		}
	}
	
	static Builder builder() {
		return new Builder();
	}

	public void offset(int x, int y) {
		xoffset = x;
		yoffset = y;
	}
	
	public Point apply(CalibrationReference ref) {
		Point p = new Point();
		p.x = (int) ((pos[0] * ref.xfactor) + ref.xoffset) + this.xoffset;
		p.y = (int) ((pos[1] * ref.yfactor) + ref.yoffset) + this.yoffset;
		return p;
	}
}