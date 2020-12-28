package jmodmenu.core;

import static jmodmenu.Utils.joaat;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StatManager {
	
	Supplier<Global> globalSupplier;
	
	public StatManager(Supplier<Global> globalSupplier) {
		this.globalSupplier = globalSupplier;
	}
	
	private Global global(int index) {
		return globalSupplier.get().at(index);
	}
	
	
	public void setStat(String statName, int statValue) {
		setStat(joaat(statName), statValue);
	}

	public void setStat(int statIndex, int statValue) {
		Global globalStatValue = global(939452).at(5526);
		Global globalTrigger = global(1388013).at(3, 1);
		Global globalStatIndex = global(2589533).at(15, 3).at(0, 1);
		Global globalCheckValue = global(2551544).at(276);
		Global globalCounter = global(1377236).at(1136);
		
		long previous1 = globalStatValue.get();
		
		long previous2 = globalStatIndex.get();
		if ( joaat("MP0_AWD_LAPDANCES") != previous2) {
			System.err.println("MP0_AWD_LAPDANCES not fixed");
		}
		
		long previous3 = globalTrigger.get();
		if ( joaat("MPPLY_AWD_FM_CR_MISSION_SCORE") != previous3) {
			System.err.println("MPPLY_AWD_FM_CR_MISSION_SCORE not fixed");
		}
		
		// System.out.println("Before trigger, checkvalue is : " + globalCheckValue.get());

		Consumer<Integer> setAndWait = value -> {
			globalCounter.set(1L); // accelerate counter loop :)
			globalStatValue.set( Integer.toUnsignedLong(value) );
			int loop = 0;
			int current;
			do { 
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					return;
				}
				current = (int) globalCheckValue.get();
				// System.out.println("loop"+loop+" checkvalue is : " + globalCheckValue.get());
			} while ( current != statValue && loop++ < 10 );
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				return;
			}
		};
		
		globalStatIndex.set( Integer.toUnsignedLong(statIndex) );
		globalTrigger.set( Integer.toUnsignedLong(joaat("MP0_LAP_DANCED_BOUGHT")) );
		setAndWait.accept(statValue);
		
		globalStatIndex.set( Integer.toUnsignedLong(joaat("MP0_AWD_LAPDANCES")) );
		setAndWait.accept(42);
		globalTrigger.set( Integer.toUnsignedLong(joaat("MPPLY_AWD_FM_CR_MISSION_SCORE")) );
		globalStatValue.set(previous1);
	}
	

	
}
