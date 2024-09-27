package sony.psp.atrac3plus;

/** Parameters of a group of sine waves */
public class WavesData {
	WaveEnvelope pendEnv; ///< pending envelope from the previous frame
	WaveEnvelope currEnv; ///< group envelope from the current frame
	int numWavs;          ///< number of sine waves in the group
	int startIndex;       ///< start index into global tones table for that subband

	public WavesData() {
		pendEnv = new WaveEnvelope();
		currEnv = new WaveEnvelope();
	}

	public void clear() {
		pendEnv.clear();
		currEnv.clear();
		numWavs = 0;
		startIndex = 0;
	}

	public void copy(WavesData from) {
		this.pendEnv.copy(from.pendEnv);
		this.currEnv.copy(from.currEnv);
		this.numWavs = from.numWavs;
		this.startIndex = from.startIndex;
	}
}
