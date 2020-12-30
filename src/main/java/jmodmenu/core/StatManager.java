package jmodmenu.core;

import static jmodmenu.Utils.joaat;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StatManager {
	
	Supplier<Global> globalSupplier;
	
	Global globalStatValue; 
	Global globalTrigger;
	Global globalStatIndex;
	Global globalCheckValue;
	Global globalCounter;
	
	public StatManager(Supplier<Global> globalSupplier) {
		this.globalSupplier = globalSupplier;
		
		globalStatValue = global(939452).at(5526);
		globalTrigger = global(1388013).at(3, 1);
		globalStatIndex = global(2589533).at(15, 3).at(0, 1);
		globalCheckValue = global(2551544).at(276);
		globalCounter = global(1377236).at(1136);
	}
	
	private Global global(int index) {
		return globalSupplier.get().at(index);
	}
	
	
	public void setStat(String statName, int statValue) {
		setStat(joaat(statName), statValue);
	}
	
	private int setAndWait(int value) {
		globalCounter.set(16L); // Gimme some time
		int checkValue = (int) globalCheckValue.get();
		if ( checkValue == value ) {
			globalCheckValue.set( value == 0 ? 1l : 0l );
		}
		globalStatValue.set( Integer.toUnsignedLong(value) );
		int loop = 0;
		int current;
		globalCounter.set(10L); // accelerate counter loop :)
		do { 
			current = (int) globalCheckValue.get();
			if ( current == value ) break;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				return -1;
			}
		} while ( loop++ < 20 ); // max wait time 1 sec.
//		System.out.format("Set stat[%d] to value[%d] at loop[%d]\n", );
		return loop;
	}
	
	private void inStatWriterContext( Consumer<BiConsumer<Integer, Integer>> statProcessor ) {
		// prepare context
		long previous1 = globalStatValue.get();
		long previous2 = globalStatIndex.get();
		if ( joaat("MP0_AWD_LAPDANCES") != previous2) {
			System.err.println("MP0_AWD_LAPDANCES not fixed");
		}
		long previous3 = globalTrigger.get();
		if ( joaat("MPPLY_AWD_FM_CR_MISSION_SCORE") != previous3) {
			System.err.println("MPPLY_AWD_FM_CR_MISSION_SCORE not fixed");
		}
		globalTrigger.set( Integer.toUnsignedLong(joaat("MP0_LAP_DANCED_BOUGHT")) );
		
		BiConsumer<Integer, Integer> statWriting = (statIndex, statValue) -> {
			globalStatIndex.set( Integer.toUnsignedLong(statIndex) );
			int loop = setAndWait(statValue);
			System.out.format("Set stat[%d] to value[%d] at loop[%d]\n", statIndex, statValue, loop);
		};
		
		statProcessor.accept( statWriting );
		
		// restore context
		globalStatIndex.set( Integer.toUnsignedLong(joaat("MP0_AWD_LAPDANCES")) );
		setAndWait(0);
		globalTrigger.set( Integer.toUnsignedLong(joaat("MPPLY_AWD_FM_CR_MISSION_SCORE")) );
		globalStatValue.set(previous1);
	}

	public void setStat(int statIndex, int statValue) {
		inStatWriterContext( writer -> writer.accept(statIndex, statValue) );
	}

	public void setStats(Map<Integer, Integer> stats) {
		inStatWriterContext( stats::forEach );
	}
	

	
}
