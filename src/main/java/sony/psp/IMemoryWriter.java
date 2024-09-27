package sony.psp;

public interface IMemoryWriter {
	/**
	 * Writes the next value to memory.
	 * The MemoryWriter can buffer the values before actually writing to the
	 * Memory.
	 * 
	 * MemoryWriters are created by calling the factory
	 *   MemoryWriter.getMemoryWriter(...)
	 * 
	 * When writing 8-bit values, only the lowest 8-bits of value are used.
	 * When writing 16-bit values, only the lowest 16-bits of value are used.
	 * 
	 * When the last value has been written, the flush()
	 * method has to be called in order to write any value buffered by the
	 * MemoryWriter.
	 * 
	 * @param value the value to be written.
	 */
	public void writeNext(int value);

	/**
	 * Skip n values when writing to memory.
	 * 
	 * When writing 32-bit values, the next 4*n bytes are skipped and left unchanged.
	 * When writing 16-bit values, the next 2*n bytes are skipped and left unchanged.
	 * When writing 8-bit values, the next n bytes are skipped and left unchanged.
	 * 
	 * @param n the number of values to be skipped.
	 */
	public void skip(int n);

	/**
	 * Write any value buffered by the MemoryWriter.
	 * This method has to be called when all the values has been written,
	 * as the last call to the MemoryWriter.
	 * After calling flush(), it is no longer allowed to call writeNext().
	 */
	public void flush();

	public int getCurrentAddress();
}
