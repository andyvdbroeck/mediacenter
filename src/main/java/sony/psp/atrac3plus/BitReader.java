package sony.psp.atrac3plus;

import sony.psp.Memory;

public class BitReader {
	private final Memory mem;
	private int addr;
	private int initialAddr;
	private int initialSize;
	private int size;
	private int bits;
	private int value;
	private int direction;

	public BitReader(Memory mem, int addr, int size) {
		this.mem = mem;
		this.addr = addr;
		this.size = size;
		initialAddr = addr;
		initialSize = size;
		bits = 0;
		direction = 1;
	}

	public boolean readBool() {
		return read1() != 0;
	}

	protected int nextAddr() {
		size--;
		return addr + direction;
	}

	protected int previousAddr() {
		size++;
		return addr - direction;
	}

	public int read1() {
		if (bits <= 0) {
			value = mem.read8(addr);
			addr = nextAddr();
			bits = 8;
		}
		int bit = value >> 7;
		bits--;
		value = (value << 1) & 0xFF;

		return bit;
	}

	public int read(int n) {
		int read;
		if (n <= bits) {
			read = value >> (8 - n);
			bits -= n;
			value = (value << n) & 0xFF;
		} else {
			read = 0;
			for (; n > 0; n--) {
				read = (read << 1) + read1();
			}
		}

		return read;
	}

	public int readByte() {
		if (bits == 8) {
			bits = 0;
			return value;
		}
		if (bits > 0) {
			skip(bits);
		}
		int read = mem.read8(addr);
		addr = nextAddr();

		return read;
	}

	public int getBitsLeft() {
		return (size << 3) + bits;
	}

	public int getBytesRead() {
		int bytesRead = addr - initialAddr;
		if (bits == 8) {
			bytesRead--;
		}

		return bytesRead;
	}

	public int getBitsRead() {
		return (addr - initialAddr) * 8 - bits;
	}

	public int peek(int n) {
		int read = read(n);
		skip(-n);
		return read;
	}

	public void skip(int n) {
		bits -= n;
		if (n >= 0) {
			while (bits < 0) {
				addr = nextAddr();
				bits += 8;
			}
		} else {
			while (bits > 8) {
				addr = previousAddr();
				bits -= 8;
			}
		}
		if (bits > 0) {
			value = mem.read8(addr - direction);
			value = (value << (8 - bits)) & 0xFF;
		}
	}

	public void seek(int n) {
		addr = initialAddr + n;
		size = initialSize - n;
		bits = 0;
	}

	public void setDirection(int direction) {
		this.direction = direction;
		bits = 0;
	}

	public void byteAlign() {
		if (bits > 0 && bits < 8) {
			skip(bits);
		}
	}

	public int getReadAddr() {
		if (bits == 8) {
			return addr - direction;
		}
		return addr;
	}

	public String toString() {
		return String.format("BitReader addr=0x%08X, bits=%d, size=0x%X, bits read %d", addr, bits, size, getBitsRead());
	}
}
