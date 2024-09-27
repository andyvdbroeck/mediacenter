package sony.psp.atrac3plus;

import static sony.psp.atrac3plus.Atrac3plusDecoder.ATRAC3P_FRAME_SAMPLES;
import static sony.psp.atrac3plus.Atrac3plusDecoder.ATRAC3P_SUBBAND_SAMPLES;

public class Context {
	public BitReader br;
	public Atrac3plusDsp dsp;

	public ChannelUnit channelUnits[] = new ChannelUnit[16]; ///< global channel units
	public int numChannelBlocks = 2;                         ///< number of channel blocks
	public int outputChannels;

	public Atrac gaincCtx; ///< gain compensation context
	public FFT mdctCtx;
	public FFT ipqfDctCtx; ///< IDCT context used by IPQF

	public float samples[][] = new float[2][ATRAC3P_FRAME_SAMPLES]; ///< quantized MDCT sprectrum
	public float mdctBuf[][] = new float[2][ATRAC3P_FRAME_SAMPLES + ATRAC3P_SUBBAND_SAMPLES]; ///< output of the IMDCT
	public float timeBuf[][] = new float[2][ATRAC3P_FRAME_SAMPLES]; ///< output of the gain compensation
	public float outpBuf[][] = new float[2][ATRAC3P_FRAME_SAMPLES];
}
