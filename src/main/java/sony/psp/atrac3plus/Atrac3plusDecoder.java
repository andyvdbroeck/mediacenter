package sony.psp.atrac3plus;

import static java.lang.Math.max;
import static java.lang.Math.min;

import sony.psp.Memory;
import sony.psp.IMemoryWriter;
import sony.psp.MemoryWriter;

/*
 * Based on the FFmpeg version from Maxim Poliakovski.
 * All credits go to him.
 */
public class Atrac3plusDecoder {
	public static final int AT3P_ERROR = -1;
	public static final int CH_UNIT_MONO       = 0;        ///< unit containing one coded channel
	public static final int CH_UNIT_STEREO     = 1;        ///< unit containing two jointly-coded channels
	public static final int CH_UNIT_EXTENSION  = 2;        ///< unit containing extension information
	public static final int CH_UNIT_TERMINATOR = 3;        ///< unit sequence terminator
	public static final int ATRAC3P_POWER_COMP_OFF = 15;   ///< disable power compensation
	public static final int ATRAC3P_SUBBANDS = 16;         ///< number of PQF subbands
	public static final int ATRAC3P_SUBBAND_SAMPLES = 128; ///< number of samples per subband
	public static final int ATRAC3P_FRAME_SAMPLES = ATRAC3P_SUBBANDS * ATRAC3P_SUBBAND_SAMPLES;
	public static final int ATRAC3P_PQF_FIR_LEN = 12;      ///< length of the prototype FIR of the PQF
	private Context ctx;
	

	private static int convertSampleFloatToInt16(float sample) {
		return min(max((int) (sample * 32768f + 0.5f), -32768), 32767) & 0xFFFF;
	}

	public static void writeOutput(float[][] samples, Memory outputMemory, int outputAddr, int numberOfSamples, int decodedChannels, int outputChannels) {
		IMemoryWriter writer = MemoryWriter.getMemoryWriter(outputMemory, outputAddr, numberOfSamples * 2 * outputChannels,2);
		switch (outputChannels) {
			case 1:
				for (int i = 0; i < numberOfSamples; i++) {
					int sample = convertSampleFloatToInt16(samples[0][i]);
					writer.writeNext(sample);
				}
				break;
			case 2:
				if (decodedChannels == 1) {
					// Convert decoded mono into output stereo
					for (int i = 0; i < numberOfSamples; i++) {
						int sample = convertSampleFloatToInt16(samples[0][i]);
						writer.writeNext(sample);
						writer.writeNext(sample);
					}
				} else {
					for (int i = 0; i < numberOfSamples; i++) {
						int lsample = convertSampleFloatToInt16(samples[0][i]);
						int rsample = convertSampleFloatToInt16(samples[1][i]);
						writer.writeNext(lsample);
						writer.writeNext(rsample);
					}
				}
				break;
		}
		writer.flush();
	}
	
	public int init(int bytesPerFrame, int channels, int outputChannels, int codingMode) {
		ChannelUnit.init();

		ctx = new Context();
		ctx.outputChannels = outputChannels;
		ctx.dsp = new Atrac3plusDsp();
		for (int i = 0; i < ctx.numChannelBlocks; i++) {
			ctx.channelUnits[i] = new ChannelUnit();
			ctx.channelUnits[i].setDsp(ctx.dsp);
		}

		// initialize IPQF
		ctx.ipqfDctCtx = new FFT();
		ctx.ipqfDctCtx.mdctInit(5, true, 31.0 / 32768.9);

		ctx.mdctCtx = new FFT();
		ctx.dsp.initImdct(ctx.mdctCtx);

		Atrac3plusDsp.initWaveSynth();

		ctx.gaincCtx = new Atrac();
		ctx.gaincCtx.initGainCompensation(6, 2);

		return 0;
	}

	public int decode(Memory inputMemory, int inputAddr, int inputLength, Memory outputMemory, int outputAddr) {
		int ret;

		if (ctx == null) {
			return AT3P_ERROR;
		}

		if (inputLength < 0) {
			return AT3P_ERROR;
		}
		if (inputLength == 0) {
			return 0;
		}

		ctx.br = new BitReader(inputMemory, inputAddr, inputLength);
		if (ctx.br.readBool()) {
			return AT3P_ERROR;
		}

		int chBlock = 0;
		int channelsToProcess = 0;
		while (ctx.br.getBitsLeft() >= 2) {
			int chUnitId = ctx.br.read(2);
			if (chUnitId == CH_UNIT_TERMINATOR) {
				break;
			}
			if (chUnitId == CH_UNIT_EXTENSION) {
				return AT3P_ERROR;
			}

			if (chBlock >= ctx.channelUnits.length || ctx.channelUnits[chBlock] == null) {
				return AT3P_ERROR;
			}

			ctx.channelUnits[chBlock].setBitReader(ctx.br);

			ctx.channelUnits[chBlock].ctx.unitType = chUnitId;
			channelsToProcess = chUnitId + 1;
			ctx.channelUnits[chBlock].setNumChannels(channelsToProcess);

			ret = ctx.channelUnits[chBlock].decode();
			if (ret < 0) {
				return ret;
			}

			ctx.channelUnits[chBlock].decodeResidualSpectrum(ctx.samples);
			ctx.channelUnits[chBlock].reconstructFrame(ctx);

			writeOutput(ctx.outpBuf, outputMemory, outputAddr, ATRAC3P_FRAME_SAMPLES, channelsToProcess, ctx.outputChannels);

			chBlock++;
		}

		return ctx.br.getBytesRead();
	}

	public int getNumberOfSamples() {
		return ATRAC3P_FRAME_SAMPLES;
	}
}
