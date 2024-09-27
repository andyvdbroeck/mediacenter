package sony.psp.atrac3plus;

/** Parameters of a single sine wave */
public class WaveParam {
	int freqIndex;  ///< wave frequency index
	int ampSf;      ///< quantized amplitude scale factor
	int ampIndex;   ///< quantized amplitude index
	int phaseIndex; ///< quantized phase index

	public void clear() {
		freqIndex = 0;
		ampSf = 0;
		ampIndex = 0;
		phaseIndex = 0;
	}
}
