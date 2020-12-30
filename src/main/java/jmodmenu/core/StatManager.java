package jmodmenu.core;

import static jmodmenu.Utils.joaat;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatManager {
	
	Supplier<Global> globalSupplier;
	
	Global globalStatValue; 
	Global globalTrigger;
	Global globalStatIndex;
	Global globalCheckValue;
	Global globalCounter;
	
	int jMPx_AWD_LAPDANCES, jMPx_LAP_DANCED_BOUGHT, jMPPLY_AWD_FM_CR_MISSION_SCORE;
	
	public StatManager(Supplier<Global> globalSupplier, int playerSlot) {
		this.globalSupplier = globalSupplier;
		
		globalStatValue = global(939452).at(5526);
		globalTrigger = global(1388013).at(3, 1);
		globalStatIndex = global(2589533).at(15, 3).at(0, 1);
		globalCheckValue = global(2551544).at(276);
		globalCounter = global(1377236).at(1136);
		
		boolean firstSlot = playerSlot == 0;
		jMPx_AWD_LAPDANCES     = joaat( firstSlot ? "MP0_AWD_LAPDANCES"     : "MP1_AWD_LAPDANCES" );
		jMPx_LAP_DANCED_BOUGHT = joaat( firstSlot ? "MP0_LAP_DANCED_BOUGHT" : "MP1_LAP_DANCED_BOUGHT" );
		jMPPLY_AWD_FM_CR_MISSION_SCORE = joaat("MPPLY_AWD_FM_CR_MISSION_SCORE");
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
		globalStatValue.setInt( value );
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
		if ( loop == 21 ) return -1;
		return loop;
	}
	
	
	private void inStatWriterContext( Consumer<BiConsumer<Integer, Integer>> statProcessor ) {
		// prepare context
		long previous1 = globalStatValue.get();
		long previous2 = globalStatIndex.get();
		if ( jMPx_AWD_LAPDANCES != previous2) {
			log.warn("MPx_AWD_LAPDANCES not fixed");
		}
		long previous3 = globalTrigger.get();
		if ( jMPPLY_AWD_FM_CR_MISSION_SCORE != previous3) {
			log.warn("MPPLY_AWD_FM_CR_MISSION_SCORE not fixed");
		}
		globalTrigger.setInt( jMPx_LAP_DANCED_BOUGHT );
		
		BiConsumer<Integer, Integer> statWriting = (statIndex, statValue) -> {
			globalStatIndex.setInt( statIndex );
			int loop = setAndWait(statValue);
			log.debug( String.format("Set stat[%d] to value[%d] at loop[%d]\n", statIndex, statValue, loop) );
		};
		
		statProcessor.accept( statWriting );
		
		// restore context
		globalStatIndex.setInt( jMPx_AWD_LAPDANCES );
		setAndWait(0);
		globalTrigger.setInt( jMPPLY_AWD_FM_CR_MISSION_SCORE );
		globalStatValue.set(previous1);
	}

	public void setStat(int statIndex, int statValue) {
		inStatWriterContext( writer -> writer.accept(statIndex, statValue) );
	}

	public void setStats(Map<Integer, Integer> stats) {
		inStatWriterContext( stats::forEach );
	}
	

	
}
