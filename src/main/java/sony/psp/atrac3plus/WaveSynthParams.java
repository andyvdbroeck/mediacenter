package sony.psp.atrac3plus;

import static sony.psp.atrac3plus.Atrac3plusDecoder.ATRAC3P_SUBBANDS;

public class WaveSynthParams {
	boolean tonesPresent;                                  ///< 1 - tones info present
	int amplitudeMode;                                     ///< 1 - low range, 0 - high range
	int numToneBands;                                      ///< number of PQF bands with tones
	boolean toneSharing[] = new boolean[ATRAC3P_SUBBANDS]; ///< 1 - subband-wise tone sharing flags
	boolean toneMaster[] = new boolean[ATRAC3P_SUBBANDS];  ///< 1 - subband-wise tone channel swapping
	boolean phaseShift[] = new boolean[ATRAC3P_SUBBANDS];  ///< 1 - subband-wise 180 degrees phase shifting
	int tonesIndex;                                        ///< total sum of tones in this unit
	WaveParam waves[] = new WaveParam[48];

	public WaveSynthParams() {
		for (int i = 0; i < waves.length; i++) {
			waves[i] = new WaveParam();
		}
	}
}
