package jmodmenu.memory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ptr {
	
	private static Kernel32 kernel32 = Kernel32.INSTANCE;
	
	private HANDLE handle;
	private Pointer baseAddress;
	private long offset;
	
	private String processName, moduleName;
	
	private Ptr(HANDLE handle, Pointer baseAddress) {
		this.handle = handle;
		this.baseAddress = baseAddress;
		this.offset = 0L;
	}
	
	public Ptr findSig(String stringPattern) {
		Pattern pattern = new Pattern(stringPattern);
		
		int bufferSize = 64000;
		ByteBuffer bufDest = ByteBuffer.allocateDirect(bufferSize);
        Pointer ptrDest = Native.getDirectBufferPointer(bufDest);
		
		IntByReference read = new IntByReference();
		long ptrValue = Pointer.nativeValue(baseAddress);
		long endAddress = ptrValue + 77000000;
		Pointer src = new Pointer( ptrValue );
		int readValue;
		int loop = 0;
		boolean found = false;
		do {
			// if ((loop+1) % 10 == 0) System.out.println(".");
			loop++;
			
			kernel32.ReadProcessMemory(handle, src, ptrDest, bufferSize, read);
			// byte[] memory = bufDest.array();
			readValue = read.getValue();
			
			if (readValue > pattern.size ) {
				int idx = pattern.match(bufDest, readValue);
				if ( idx > 0 ) {
					ptrValue += idx;
					Ptr ptr = copy();
					ptr.offset = ptrValue - Pointer.nativeValue(baseAddress);
					return ptr;
				}
			}
			
			ptrValue += readValue;
			Pointer.nativeValue(src, ptrValue);
		} while ( readValue > 0 || ptrValue < endAddress || !found );
		
		return null;
	}
	
	public Ptr add(int val) {
		offset += val;
		return this;
	}
	
	public Ptr sub(int val) {
		offset -= val;
		return this;
	}
	
	public Ptr rip() {
		Memory memory = getMemory(4);
		offset += memory.getInt(0) + 4;
		return this;
	}
	
	public long readLong() {
		Memory memory = getMemory(8);
		return memory.getLong(0);
	}
	
	public float readFloat() {
		Memory memory = getMemory(8);
		return memory.getFloat(0);
	}
	
	public int readInt() {
		Memory memory = getMemory(4);
		return memory.getInt(0);
	}

	public String readString(int maxSize) {
		Memory memory = getMemory(maxSize);
		return memory.getString(0, "UTF-8");
	}
	
	public Memory getMemory(int size) {
		Memory memory = new Memory(size);
		Pointer src = baseAddress.share(offset);
		kernel32.ReadProcessMemory(handle, src, memory, size, null);
		return memory;
	}

	public boolean writeFloat(float value) {
		Memory memory = new Memory(8);
		memory.setFloat(0, value);
		Pointer src = baseAddress.share(offset);
		return kernel32.WriteProcessMemory(handle, src, memory, 8, null);
	}

	public boolean writeLong(long value) {
		Memory memory = new Memory(8);
		memory.setLong(0, value);
		Pointer src = baseAddress.share(offset);
		IntByReference intRef = new IntByReference();
		boolean res = kernel32.WriteProcessMemory(handle, src, memory, 8, intRef);
		// boolean res = false;
		
		// System.out.format( "writeLong to %s: %s %d bytes wrote. dump:\n%s ", this, res, intRef.getValue(), memory.dump());
		return res;
	}

	public boolean writeInt(int value) {
		Memory memory = new Memory(4);
		memory.setInt(0, value);
		Pointer src = baseAddress.share(offset);
		IntByReference intRef = new IntByReference();
		boolean res = kernel32.WriteProcessMemory(handle, src, memory, 4, intRef);
		// boolean res = false;
		
		// System.out.format( "writeLong to %s: %s %d bytes wrote. dump:\n%s ", this, res, intRef.getValue(), memory.dump());
		return res;
	}

	public boolean writeString(String str) {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(str.length() + 1);
		PrintWriter out = new PrintWriter( bos, true, Charset.forName("ISO-8859-1") );
		out.print(str);
		out.flush();
		bos.write(0);
		byte[] buffer = bos.toByteArray();
		
		Memory memory = new Memory(buffer.length);
		memory.write(0, buffer, 0, buffer.length);
		
		Pointer src = baseAddress.share(offset); // new Pointer( Pointer.nativeValue(baseAddress) + offset );
		return kernel32.WriteProcessMemory(handle, src, memory, buffer.length, null);
		
	}
	
	public Ptr copy() {
		Ptr ptr = new Ptr(handle, baseAddress);
		ptr.offset = offset;
		ptr.moduleName = moduleName;
		ptr.processName = processName;
		return ptr;
	}
	
	public Ptr indirect64() {
		baseAddress = new Pointer( readLong() );
		// System.out.println( "Indirect from " + src + " to " + baseAddress );
		offset = 0;
		return this;
	}
	
	@Override
	public String toString() {
		return moduleName
			+ "["+String.format("%#08x", Pointer.nativeValue(baseAddress))
			+ "]+0x"+Long.toHexString(offset)
			+ " => 0x"+Long.toHexString(Pointer.nativeValue(baseAddress) + offset);
	}
	
	public static Ptr ofProcess(String processName) {
		int pid = ProcessHandle.allProcesses()
				.filter( p -> p.info().command().orElse("").endsWith(processName) )
				.map( p -> p.pid() )
				.findFirst()
				.orElseThrow( () -> new RuntimeException("No process '"+processName+"' found") )
				.intValue();
		
		log.info("Process {} found. Pid[{}]", processName, pid);
		
		// https://docs.microsoft.com/en-us/windows/win32/procthread/process-security-and-access-rights
		int accessRight = 0x0010 | 0x0020 | 0x0008;
		HANDLE handle = kernel32.OpenProcess(accessRight, false, pid);
		
		Pointer baseAddress = getModuleBaseAddress(pid, processName);
		
		Ptr ptr = new Ptr(handle, baseAddress);
		ptr.processName = processName;
		ptr.moduleName = processName;
		return ptr;
	}

	public static Pointer getModuleBaseAddress(int pid, String moduleName) {
		Pointer baseAddress = null;
		HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new DWORD(pid));
		try {
			Tlhelp32.MODULEENTRY32W module = new Tlhelp32.MODULEENTRY32W();
			if ( kernel32.Module32FirstW(snapshot, module) ) {
				do {
					if ( moduleName.equals(module.szModule()) ) {
						baseAddress = module.modBaseAddr;
						
						log.debug( "Module address : {}", module.modBaseAddr );
						log.debug( "Module size    : {}", module.modBaseSize );
						
						break;
					}
				} while ( kernel32.Module32NextW(snapshot, module) );
			}
		} finally {			
			Kernel32Util.closeHandle(snapshot);
		}
		
		return baseAddress;
	}

	public void dumpAround(int i) {
		int size = i*2 + 1;
		Memory memory = new Memory(size);
		Pointer src = new Pointer( Pointer.nativeValue(baseAddress) + offset - i);
		kernel32.ReadProcessMemory(handle, src, memory, size, null);
		for(int j = 0; j < size; j++) {
			int b = 0x00FF & memory.getByte(j);
			String v = (b < 16 ? "0" : "") + Integer.toHexString(b);
			System.out.print(v);
			if ( j == i ) {
				System.out.print( "]");
			} else if (j+1 == i){				
				System.out.print( "[");
			} else {
				System.out.print( " ");
			}
			if ( (j+1) % 16 == 0 ) System.out.println();
		}
	}

	public void dumpfrom(int size) {
		Memory memory = new Memory(size);
		Pointer src = baseAddress.share( offset );
		kernel32.ReadProcessMemory(handle, src, memory, size, null);
		String line = "";
		for(int j = 0; j < size; j++) {
			if ( j % 16 == 0 ) {
				String adrStr = String.format("%#06x", j);
				System.out.print( adrStr+" " );
			}
			int b = 0x00FF & memory.getByte(j);
			String v = (b < 16 ? "0" : "") + Integer.toHexString(b);
			line += ((char)(b > 32 && b < 254 ? b : '_'))+" ";
			System.out.print(v);
			System.out.print( " ");
			if ( (j+1) % 16 == 0 ) {
				System.out.println( " " + line );
				line = "";
			}
		}
	}
}

class Pattern {
	byte[] bytePattern;
	boolean[] mask;
	int size;
	
	Pattern(String pattern) {
		bytePattern = new byte[32];
		mask = new boolean[32];
		
		Arrays.fill(mask, true);
		
		int length = pattern.length();
		size = 0;
		for (int i = 0; i < length; i++) {
			char c = pattern.charAt(i);
			if ( c == ' ' ) continue;
			if ( c == '?' ) {
				mask[size++] = false;
				continue;
			}
			bytePattern[size++] = 
				(byte) (( Character.digit(pattern.charAt(i++), 16) << 4 ) + Character.digit(pattern.charAt(i++), 16));
		}
	}
	
	int getSize() {
		return size;
	}
	
	int match(ByteBuffer buffer, int toIndex) {
		for ( int j = 0; j < (toIndex-size); j++) {
			int i = 0;
			for (; i < size && (!mask[i] || buffer.get(i+j) == bytePattern[i]); i++);
			if (i == size) return j;
		}
		return -1;
	}
}