package jmodmenu.cayo_perico.model;

import java.security.InvalidParameterException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public interface MapItem {
	
	String name();
	float[] position();
	
	default float[] f(float x, float y, float z) {
		return new float[] {x, y, z};
	}
	
	static void globalBitStream(int size, int globalValue, IntConsumer visitor) {
		bitStream(size, globalValue).forEach(visitor);
	}
	
	static IntStream bitStream(int size, int global) {
		if ( size > 32 ) {
			throw new InvalidParameterException("Int size is 32 bits maximum. requested: " + size);
		}
		return IntStream.range(0, size)
				.filter( i -> (global & (1 << i)) > 0);
	}

}
