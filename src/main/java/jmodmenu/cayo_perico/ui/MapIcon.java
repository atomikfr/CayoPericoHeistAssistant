package jmodmenu.cayo_perico.ui;

import java.awt.Color;

class MapIcon {
	float[] pos = new float[3];
	Color color = Color.WHITE;
	int xoffset, yoffset;
	
	static class Builder {
		MapIcon mapIcon = new MapIcon();
		Builder pos(float x, float y, float z) {
			mapIcon.pos[0] = x;
			mapIcon.pos[1] = y;
			mapIcon.pos[2] = z;
			return this;
		}
		Builder color(Color color) {
			mapIcon.color = color;
			return this;
		}
		MapIcon build() {
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
}