package sony.psp;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import com.twilight.h264.decoder.AVFrame;
import com.twilight.h264.decoder.AVPacket;
import com.twilight.h264.decoder.H264Context;
import com.twilight.h264.decoder.H264Decoder;
import com.twilight.h264.decoder.MpegEncContext;

import sony.psp.atrac3plus.Atrac3plusDecoder;

public class PMFPlayer implements Runnable {
	
	public static final int ATRAC3P_SUBBANDS = 16;         ///< number of PQF subbands
	public static final int ATRAC3P_SUBBAND_SAMPLES = 128; ///< number of samples per subband
	public static final int ATRAC3P_FRAME_SAMPLES = ATRAC3P_SUBBANDS * ATRAC3P_SUBBAND_SAMPLES;
	public static final int START_USERSPACE = 0x08800000; // KU0
	public static final int PSP_CODEC_AT3PLUS = 0x00001000;
    public static final int PSMF_LAST_TIMESTAMP_OFFSET = 0x5A;
    public static final int PSMF_FIRST_TIMESTAMP_OFFSET = 0x54;
	public static final int videoTimestampStep = 3003;
	private static final int progressHeight = 2;

	private boolean running = true;
	private Image lastFrame;
	private int[] buffer = null;
	private int[] videoData = new int[0x10000];
	private int[] audioData = new int[0x10000];
	private int videoDataOffset;
	private int audioDataOffset;
	private int audioFrameLength;
    private final int frameHeader[] = new int[8];
    private int frameHeaderLength;
	private InputStream is;
	private int totalNumberOfFrames;

	public PMFPlayer(InputStream input) {
		is = input;
	}
	
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		running = false;
	}
	
	@Override
	public void run() {
		playFile();		
	}

	private int read8(InputStream is) {
		try {
			return is.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	private int read16(InputStream is) {
		return (read8(is) << 8) | read8(is);
	}

	private int read32(InputStream is) {
		return (read8(is) << 24) | (read8(is) << 16) | (read8(is) << 8) | read8(is);
	}

	private long readTimestamp(InputStream is) {
		long timestamp = (read16(is) & 0xFFFFL) << 32;
		timestamp |= read32(is) & 0xFFFFFFFFL;

		return timestamp;
	}

	private void skip(InputStream is, int n) {
		if (n > 0) {
			try {
				is.skip(n);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int skipPesHeader(InputStream is, int startCode) {
		int pesLength = 0;
		int c = read8(is);
		pesLength++;
		while (c == 0xFF) {
			c = read8(is);
			pesLength++;
		}

		if ((c & 0xC0) == 0x40) {
			skip(is, 1);
			c = read8(is);
			pesLength += 2;
		}

		if ((c & 0xE0) == 0x20) {
			skip(is, 4);
			pesLength += 4;
			if ((c & 0x10) != 0) {
				skip(is, 5);
				pesLength += 5;
			}
		} else if ((c & 0xC0) == 0x80) {
			skip(is, 1);
			int headerLength = read8(is);
			pesLength += 2;
			skip(is, headerLength);
			pesLength += headerLength;
		}

		if (startCode == 0x1BD) { // PRIVATE_STREAM_1
			int channel = read8(is);
			pesLength++;
			if (channel >= 0x80 && channel <= 0xCF) {
				skip(is, 3);
				pesLength += 3;
				if (channel >= 0xB0 && channel <= 0xBF) {
					skip(is, 1);
					pesLength++;
				}
			} else {
				skip(is, 3);
				pesLength += 3;
			}
		}

		return pesLength;
	}

	private void addVideoData(InputStream is, int length) {
		if (videoDataOffset + length > videoData.length) {
			// Extend the inputBuffer
			int[] newInputBuffer = new int[videoDataOffset + length];
			System.arraycopy(videoData, 0, newInputBuffer, 0, videoDataOffset);
			videoData = newInputBuffer;
		}

		for (int i = 0; i < length; i++) {
			videoData[videoDataOffset++] = read8(is);
		}
	}

	private void addAudioData(InputStream is, int length) {
		if (audioDataOffset + length > audioData.length) {
			// Extend the inputBuffer
			int[] newInputBuffer = new int[audioDataOffset + length];
			System.arraycopy(audioData, 0, newInputBuffer, 0, audioDataOffset);
			audioData = newInputBuffer;
		}

		while (length > 0) {
    		int currentFrameLength = audioFrameLength == 0 ? 0 : audioDataOffset % audioFrameLength;
    		if (currentFrameLength == 0) {
    			// 8 bytes header:
    			// - byte 0: 0x0F
    			// - byte 1: 0xD0
    			// - byte 2: 0x28
    			// - byte 3: (frameLength - 8) / 8
    			// - bytes 4-7: 0x00
    			while (frameHeaderLength < frameHeader.length && length > 0) {
    				frameHeader[frameHeaderLength++] = read8(is);
    				length--;
    			}
    			if (frameHeaderLength < frameHeader.length) {
    				// Frame header not yet complete
    				break;
    			}
    			if (length == 0) {
    				// Frame header is complete but no data is following the header.
    				// Retry when some data is available
    				break;
    			}

    			int frameHeader23 = (frameHeader[2] << 8) | frameHeader[3];
    			audioFrameLength = ((frameHeader23 & 0x3FF) << 3) + 8;
    			frameHeaderLength = 0;
    		}

    		int lengthToNextFrame = audioFrameLength - currentFrameLength;
    		int readLength = Math.min(length, lengthToNextFrame);
    		for (int i = 0; i < readLength; i++) {
    			audioData[audioDataOffset++] = read8(is);
    		}
    		length -= readLength;
		}
	}

	private void readPsmfHeader() {
		if (read32(is) != 0x50534D46) { // PSMF
			return;
		}
		skip(is, 4);
		int mpegOffset = read32(is);
		skip(is, PSMF_FIRST_TIMESTAMP_OFFSET - 12);
		long firstTimestamp = readTimestamp(is);
		long lastTimestamp = readTimestamp(is);
		totalNumberOfFrames = (int) ((lastTimestamp - firstTimestamp) / videoTimestampStep);
		skip(is, mpegOffset - (PSMF_LAST_TIMESTAMP_OFFSET + 6));
	}

	private boolean readPsmfPacket(int videoChannel, int audioChannel) {
		while (true) {
			int startCode = read32(is);
			if (startCode == -1) {
				// End of file
				return false;
			}
			int codeLength;
			switch (startCode) {
				case 0x1BA: // PACK_START_CODE
					skip(is, 10);
					break;
				case 0x1BB: // SYSTEM_HEADER_START_CODE
					skip(is, 14);
					break;
				case 0x1BE: // PADDING_STREAM
				case 0x1BF: // PRIVATE_STREAM_2
					codeLength = read16(is);
					skip(is, codeLength);
					break;
				case 0x1BD: { // PRIVATE_STREAM_1
					// Audio stream
					codeLength = read16(is);
					int pesLength = skipPesHeader(is, startCode);
					codeLength -= pesLength;
					addAudioData(is, codeLength);
					break;
				}
				case 0x1E0: case 0x1E1: case 0x1E2: case 0x1E3: // Video streams
				case 0x1E4: case 0x1E5: case 0x1E6: case 0x1E7:
				case 0x1E8: case 0x1E9: case 0x1EA: case 0x1EB:
				case 0x1EC: case 0x1ED: case 0x1EE: case 0x1EF:
					codeLength = read16(is);
					if (videoChannel < 0 || startCode - 0x1E0 == videoChannel) {
						int pesLength = skipPesHeader(is, startCode);
						codeLength -= pesLength;
						addVideoData(is, codeLength);
						return true;
					}
					skip(is, codeLength);
					break;
			}
		}
	}

	private void consumeVideoData(int length) {
		if (length >= videoDataOffset) {
			videoDataOffset = 0;
		} else {
			System.arraycopy(videoData, length, videoData, 0, videoDataOffset - length);
			videoDataOffset -= length;
		}
	}

	private void consumeAudioData(int length) {
		if (length >= audioDataOffset) {
			audioDataOffset = 0;
		} else {
			System.arraycopy(audioData, length, audioData, 0, audioDataOffset - length);
			audioDataOffset -= length;
		}
	}

	private int findFrameEnd() {
		for (int i = 5; i < videoDataOffset; i++) {
			if (videoData[i - 4] == 0x00 &&
			    videoData[i - 3] == 0x00 &&
			    videoData[i - 2] == 0x00 &&
			    videoData[i - 1] == 0x01) {
				int naluType = videoData[i] & 0x1F;
				if (naluType == H264Context.NAL_AUD) {
					return i - 4;
				}
			}
		}

		return -1;
	}

	public boolean playFile() {
        H264Decoder codec;
	    MpegEncContext c= null;
	    int[] got_picture = new int[1];
	    AVFrame picture;
	    AVPacket avpkt = new AVPacket();

	    avpkt.av_init_packet();

	    /* find the mpeg1 video decoder */
	    codec = new H264Decoder();

	    c = MpegEncContext.avcodec_alloc_context();
	    picture = AVFrame.avcodec_alloc_frame();

	    if ((codec.capabilities & H264Decoder.CODEC_CAP_TRUNCATED) != 0) {
	        c.flags |= MpegEncContext.CODEC_FLAG_TRUNCATED; /* we do not send complete frames */
	    }

	    /* open it */
	    c.avcodec_open(codec);

	    boolean showProgress = true;
	    int frame = 0;
	    try {
	    	readPsmfHeader();
	    	if (totalNumberOfFrames <= 0) {
	    		showProgress = false;
	    	}
	    	int videoChannel = 0;
	    	int audioChannel = 0;
	    	int audioChannels = 2;

			Memory mem = Memory.getInstance();
			int audioInputAddr = START_USERSPACE + 0x10000;
			int audioOutputAddr = START_USERSPACE;
			Atrac3plusDecoder audioCodec = new Atrac3plusDecoder();
		    boolean audioCodecInit = false;
		    byte[] audioOutputData = null;
	        AudioFormat audioFormat = new AudioFormat(44100,
	                16,
	                audioChannels,
	                true,
	                false);
	        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine mLine = (SourceDataLine) AudioSystem.getLine(info);
            mLine.open(audioFormat);
            mLine.start();

            long startTime = System.currentTimeMillis();

	    	while (running) {
			    int frameSize = -1;
			    do {
			    	if (!readPsmfPacket(videoChannel, audioChannel)) {
			    		if (videoDataOffset <= 0) {
			    			// End of file reached
			    			break;
			    		}
			    		frameSize = findFrameEnd();
			    		if (frameSize < 0) {
				    		// Process pending last frame
			    			frameSize = videoDataOffset;
			    		}
			    	} else {
			    		frameSize = findFrameEnd();
			    	}
			    } while (frameSize <= 0);

			    if (frameSize <= 0) {
			    	break;
			    }
			    avpkt.data_base = videoData;
		        avpkt.size = frameSize;
		        avpkt.data_offset = 0;

		        while (avpkt.size > 0) {
		            int len = c.avcodec_decode_video2(picture, got_picture, avpkt);
		            if (len < 0) {
		                // Discard current packet and proceed to next packet
		                break;
		            } // if
		            if (got_picture[0] != 0) {
		            	picture = c.priv_data.displayPicture;
		            	picture.imageHeightWOEdge -=20;
		            	int imageWidth = picture.imageWidthWOEdge;
		            	int imageHeight = picture.imageHeightWOEdge;
						int bufferSize = imageWidth * imageHeight;
						if (buffer == null || bufferSize != buffer.length) {
							buffer = new int[bufferSize];
						}
						PMFPlayer.YUV2RGB_WOEdge(picture, buffer);

						int progress = showProgress ? imageWidth * frame / totalNumberOfFrames : 0;
						int y = Math.min(272, imageHeight - progressHeight);
						int offset = y * imageWidth;
						for (int i = 0; i < progressHeight; i++, offset += imageWidth) {
							Arrays.fill(buffer, offset, offset + progress, 0xFFFFFFFF);
							Arrays.fill(buffer, offset + progress, offset + imageWidth, 0xFF000000);
						}

						lastFrame = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(imageWidth
								, imageHeight, buffer, 0, imageWidth));
						update();
						frame++;

					    long now = System.currentTimeMillis();
					    long currentDuration = now - startTime;
					    long videoDuration = frame * 100000L / 3003L;
					    if (currentDuration < videoDuration) {
					    	Thread.sleep(videoDuration - currentDuration);
					    }

					    now = System.currentTimeMillis();
		            }
		            avpkt.size -= len;
		            avpkt.data_offset += len;
		        }

		        consumeVideoData(frameSize);

		        if (audioOutputData != null) {
		        	if (mLine.available() >= audioOutputData.length) {
		        		mLine.write(audioOutputData, 0, audioOutputData.length);
		        		audioOutputData = null;
		        	}
		        } else if (audioFrameLength > 0) {
		        	if (!audioCodecInit) {
		        		audioCodec.init(audioFrameLength, audioChannels, audioChannels, 0);
		        		audioCodecInit = true;
		        	}
		        	while (audioDataOffset >= audioFrameLength) {
		        		for (int i = 0; i < audioFrameLength; i++) {
		        			mem.write8(audioInputAddr + i, (byte) audioData[i]);
		        		}
		        		int result = audioCodec.decode(mem, audioInputAddr, audioFrameLength, mem, audioOutputAddr);
		        		if (result < 0) {
		        			break;
		        		} else if (result == 0) {
		        			break;
		        		} else if (result > 0) {
		        			consumeAudioData(audioFrameLength);

		        			audioOutputData = new byte[ATRAC3P_FRAME_SAMPLES * 2 * audioChannels];
		        			for (int i = 0; i < audioOutputData.length; i++) {
		        				audioOutputData[i] = (byte) mem.read8(audioOutputAddr + i);
		        			}

		        			if (mLine.available() < audioOutputData.length) {
				        		break;
				        	}
			        		mLine.write(audioOutputData, 0, audioOutputData.length);
			        		audioOutputData = null;
		        		}
		        	}
		        }
			} // while

	    	mLine.drain();
	    	mLine.close();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	try { is.close(); } catch(Exception ee) {}
	    } // try

	    c.avcodec_close();
	    c = null;
	    picture = null;
	    return true;
	}
	
	public static void YUV2RGB_WOEdge(AVFrame f, int[] rgb) {
		int[] luma = f.data_base[0];
		int[] cb = f.data_base[1];
		int[] cr = f.data_base[2];

		for (int y = 0; y < f.imageHeightWOEdge; y++) {
			int lineOffLuma =  y       * f.linesize[0] + f.data_offset[0];
			int lineOffCb   = (y >> 1) * f.linesize[1] + f.data_offset[1];
			int lineOffCr   = (y >> 1) * f.linesize[2] + f.data_offset[2];
			int rgbOff = y * f.imageWidthWOEdge;

			for (int x = 0; x < f.imageWidthWOEdge; x++) {
				int c = luma[lineOffLuma + x] - 16;
				int d = cb[lineOffCb + (x >> 1)] - 128;
				int e = cr[lineOffCr + (x >> 1)] - 128;

				int red = (298 * c + 409 * e + 128) >> 8;
				red = red < 0 ? 0 : (red > 255 ? 255 : red);
				int green = (298 * c - 100 * d - 208 * e + 128) >> 8;
				green = green < 0 ? 0 : (green > 255 ? 255 : green);
				int blue = (298 * c + 516 * d + 128) >> 8;
				blue = blue < 0 ? 0 : (blue > 255 ? 255 : blue);
				int alpha = 255;

				rgb[rgbOff + x] = (alpha << 24) | (red << 16) | (green << 8) | blue;
			}
		}
	}
	
	public void update() {
		// need to be overridden to get the current frame
	}

	public Image getLastFrame() {
		return lastFrame;
	}
}
