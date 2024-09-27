package sony.psp.atrac3plus;

public class WaveEnvelope {
	boolean hasStartPoint; ///< indicates start point within the GHA window
	boolean hasStopPoint;  ///< indicates stop point within the GHA window
	int startPos;          ///< start position expressed in n*4 samples
	int stopPos;           ///< stop  position expressed in n*4 samples

	public void clear() {
		hasStartPoint = false;
		hasStopPoint = false;
		startPos = 0;
		stopPos = 0;
	}

	public void copy(WaveEnvelope from) {
		this.hasStartPoint = from.hasStartPoint;
		this.hasStopPoint = from.hasStopPoint;
		this.startPos = from.startPos;
		this.stopPos = from.stopPos;
	}
}
