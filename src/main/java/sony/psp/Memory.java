package sony.psp;

import java.nio.Buffer;

import com.sun.jna.Pointer;

public class Memory {
	
	private static Memory mem = null;
    public static final int addressMask = 0x1FFFFFFF;
    protected static final int MEMORY_PAGE_SHIFT = 12;
    protected static final boolean[] validMemoryPage = new boolean[1 << (Integer.SIZE - MEMORY_PAGE_SHIFT)];
    
    private Pointer p = new com.sun.jna.Memory(PMFPlayer.START_USERSPACE + 0x10000 + PMFPlayer.ATRAC3P_FRAME_SAMPLES * 4);
    
	public Buffer getBuffer(int address, int length) {
		return p.getByteBuffer(address, length);
	}
	
	public static Memory getInstance() {
		if(mem==null) {
			mem = new Memory();
		}
		return mem;
	}

    public static boolean isAddressGood(int address) {
		return validMemoryPage[address >>> MEMORY_PAGE_SHIFT];
    }
	
	public int[] getMemoryInt(int address) {
		return null;
	}
	
	private int reverseBytes32(int n) {
		return ((n >> 24) & 0xFF) | ((n >> 8) & 0xFF00) | ((n << 8) & 0xFF0000) | (n << 24);
	}

	private int reverseBytes16(short n) {
		return (n >> 8) | (n << 8);
	}
	
	public int read8(int address) {
		return p.getInt(address);
	}

    public void write8(int address, byte data) {
    	p.setInt(address & ~1,  data /*& 0xff*/);
    }

    public void write16(int address, short data) {
    	p.setInt(address & ~1, reverseBytes16(data) /*& 0xff*/);
    }

    public void write32(int address, int data) {
    	p.setInt(address & ~3, reverseBytes32(data) /*& 0xff*/);
    }
    
}
