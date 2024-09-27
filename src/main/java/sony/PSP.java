package sony;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import sony.psp.PMFPlayer;
import sony.xmb.Background;
import sony.xmb.Battery;
import sony.xmb.Clock;
import sony.xmb.Volume;

public class PSP extends Pane implements Base {

	public PSP() {
		bg = new Background();
		status = new HBox(8D);
		clock = new Clock();
		battery = new Battery();
		pointer = new ImageView();
		pics = new ImageView[5];
		icon = new ImageView();
		files = new File[5];
		filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().toUpperCase().endsWith("ISO");
			}
		};
		getChildren().add(bg);
		status.getChildren().add(clock);
		status.getChildren().add(battery);
		getChildren().add(status);
		DropShadow effect = new DropShadow();
		effect.setOffsetX(0.0D);
		effect.setOffsetY(0.0D);
		effect.setSpread(0.5D);
		effect.setColor(Color.WHITE);
		pointer.setImage(new Image(getClass().getResourceAsStream("/sony/img/pointer.png")));
		pointer.setEffect(effect);
		getChildren().add(pointer);
		getChildren().add(Volume.getInstance());
		pics[0] = new ImageView();
		getChildren().add(pics[0]);
		pics[1] = new ImageView();
		getChildren().add(pics[1]);
		pics[2] = new ImageView();
		final DropShadow dropShadow = new DropShadow();
		dropShadow.setOffsetX(0.0D);
		dropShadow.setOffsetY(0.0D);
		dropShadow.setColor(Color.WHITE);
		KeyFrame kf = new KeyFrame(Duration.millis(80D), new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				if (direction) {
					dropShadow.setRadius(dropShadow.getRadius() - 10D);
					dropShadow.setSpread(dropShadow.getSpread() - 0.1D);
				} else {
					dropShadow.setRadius(dropShadow.getRadius() + 10D);
					dropShadow.setSpread(dropShadow.getSpread() + 0.1D);
				}
				if (dropShadow.getSpread() > 0.5D)
					direction = true;
				if (dropShadow.getSpread() < 0.0D)
					direction = false;
			}

			private boolean direction = true;
		});
		Timeline repeat = new Timeline(kf);
		repeat.setCycleCount(Timeline.INDEFINITE);
		repeat.play();
		pics[2].setEffect(dropShadow);
		getChildren().add(pics[2]);
		pics[3] = new ImageView();
		getChildren().add(pics[3]);
		pics[4] = new ImageView();
		getChildren().add(pics[4]);
		getChildren().add(icon);
		File currentDir = new File(Main.getUserHome() + "/Sony/Game");
		if (!currentDir.exists())
			currentDir.mkdirs();
		File dir[] = currentDir.listFiles(filter);
		if (dir.length > 0)
			setFile(dir[0]);
	}

	private int getPosition(File file, File list[]) {
		int nbr = 0;
		int i = 0;
		do {
			if (i >= list.length)
				break;
			if (list[i].getName().equals(file.getName())) {
				nbr = i + 1;
				break;
			}
			i++;
		} while (true);
		return nbr;
	}

	private void playSound() {
		try {
			Media m = new Media(sony.Controller.class.getResource("/sony/snd/move.mp3").toString());
			MediaPlayer mp = new MediaPlayer(m);
			mp.play();
		} catch (Exception ex) {
		}
	}

	private void updateISOdata() {
		boolean iconFlag = true;
		boolean bgFlag = true;
		boolean pmfFlag = true;
		for (int i = 0; i < 5; i++)
			if (files[i] != null)
				try {
					final int index = i;
					boolean picFlag = true;
					RandomAccessFile randomAccessFile = new RandomAccessFile(files[index], "r");
					IInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.ISO,
							new RandomAccessFileInStream(randomAccessFile));
					ISimpleInArchive sa = inArchive.getSimpleInterface();
					for (ISimpleInArchiveItem item : sa.getArchiveItems()) {
						if ("PSP_GAME\\ICON0.PNG".equals(item.getPath())) {
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							ExtractOperationResult result = item.extractSlow(new ISequentialOutStream() {
								public int write(byte[] data) throws SevenZipException {
									try {
										bos.write(data);
									} catch (IOException e) {
									}
									return data.length;
								}
							});
							if (result == ExtractOperationResult.OK) {
								InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
								pics[index].setImage(new Image(inputStream, 800D, 600D, false, false));
								try {
									inputStream.close();
								} catch (IOException e) {
								}
								picFlag = false;
							}
							bos.close();
						}
						if (index == 2 && "PSP_GAME\\PIC0.PNG".equals(item.getPath())) {
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							ExtractOperationResult result = item.extractSlow(new ISequentialOutStream() {
								public int write(byte[] data) throws SevenZipException {
									try {
										bos.write(data);
									} catch (IOException e) {
									}
									return data.length;
								}
							});
							if (result == ExtractOperationResult.OK) {
								iconFlag = false;
								InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
								icon.setImage(new Image(inputStream, 800D, 600D, false, false));
								inputStream.close();
							}
							bos.close();
						}
						if (index == 2 && "PSP_GAME\\PIC1.PNG".equals(item.getPath())) {
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							ExtractOperationResult result = item.extractSlow(new ISequentialOutStream() {
								public int write(byte[] data) throws SevenZipException {
									try {
										bos.write(data);
									} catch (IOException e) {
									}
									return data.length;
								}
							});
							if (result == ExtractOperationResult.OK) {
								bgFlag = false;
								InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
								bg.setImage(new Image(inputStream));
								inputStream.close();
							}
							bos.close();
						}
						if (index == 2 && "PSP_GAME\\ICON1.PMF".equals(item.getPath())) {
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							ExtractOperationResult result = item.extractSlow(new ISequentialOutStream() {
								public int write(byte[] data) throws SevenZipException {
									try {
										bos.write(data);
									} catch (IOException e) {
									}
									return data.length;
								}
							});
							if (result == ExtractOperationResult.OK) {
								pmfFlag = false;
								InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
								player = new PMFPlayer(inputStream) {
									@Override
									public void update() {
										pics[index].setImage(convertToFxImage(getLastFrame()));
									}
								};
								player.start();
								inputStream.close();
							}
							bos.close();
						}
						if ((index != 2 && picFlag == false) || (index == 2 && picFlag == false && bgFlag == false
								&& iconFlag == false && pmfFlag == false)) {
							continue;
						}
					}
					inArchive.close();
					randomAccessFile.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					bg.setImage(Main.getMenu().getTheme());
				}
			else
				pics[i].setImage(null);
		if (iconFlag) {
			icon.setImage(null);
		}
		if (bgFlag) {
			bg.setImage(Main.getMenu().getTheme());
		}
	}

	private static Image convertToFxImage(java.awt.Image image) {
		if (!(image instanceof RenderedImage)) {
			BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
					BufferedImage.TYPE_INT_ARGB);
			Graphics g = bufferedImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();

			image = bufferedImage;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write((RenderedImage) image, "png", out);
			out.flush();
		} catch (IOException e) {}
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return new javafx.scene.image.Image(in);
	}

	public void setFile(File file) {
		files[2] = file;
		File list[] = file.getParentFile().listFiles(filter);
		int index = getPosition(files[2], list) - 1;
		files[0] = list.length <= index - 2 || index - 2 < 0 ? null : list[index - 2];
		files[1] = list.length <= index - 1 || index - 1 < 0 ? null : list[index - 1];
		files[3] = list.length <= index + 1 || index + 1 < 0 ? null : list[index + 1];
		files[4] = list.length <= index + 2 || index + 2 < 0 ? null : list[index + 2];
		updateISOdata();
	}

	public void left() {
		stopPlayer();
		playSound();
		exit();
	}

	public void right() {
		stopPlayer();
		playSound();
		enter();
	}

	public void up() {
		stopPlayer();
		playSound();
		if (files[1] != null)
			setFile(files[1]);
	}

	public void down() {
		stopPlayer();
		playSound();
		if (files[3] != null)
			setFile(files[3]);
	}

	public void enter() {
		stopPlayer();
		playSound();
		try {
			SystemTray.minimizeToSystemTray();
			String vbsfile = "\"" + Utils.getJarFolder() + "emulator_bat.vbs\"";
			String batfile = "\"" + Utils.getJarFolder() + "emulator.bat\"";
			String isofile = "\"" + files[2].getPath() + "\"";
			String emulator = "WScript.exe " + vbsfile + " " + batfile + " " + isofile;
			Runtime.getRuntime().exec(emulator);
		} catch (IOException ioe) {
		}
		Main.getMenu().update();
	}

	public void update() {
		if (!getChildren().contains(Volume.getInstance()))
			getChildren().add(Volume.getInstance());
		Scene scene = getScene();
		if (scene != null) {
			bg.setFitWidth(scene.getWidth());
			bg.setFitHeight(scene.getHeight());
			status.setLayoutX(0.0D);
			status.setLayoutY(0.0D);
			status.setPrefHeight(80D);
			status.setAlignment(Pos.CENTER_RIGHT);
			status.setPrefWidth(scene.getWidth());
			clock.setFont(new Font(scene.getHeight() / 20D));
			battery.setFitWidth(scene.getWidth() / 10D);
			battery.setFitHeight(scene.getHeight() / 10D);
			double width = getScene().getWidth();
			double height = getScene().getHeight();
			double box = getScene().getHeight() / 7D;
			double marginX = box / 4D + width / 12D;
			double marginY = getScene().getHeight() / 6D;
			pointer.setLayoutX(0.0D);
			pointer.setLayoutY(height / 2D - box / 5D);
			pointer.setFitHeight(box / 5D);
			pointer.setFitWidth(box / 5D);
			pics[0].setLayoutX(marginX);
			pics[0].setLayoutY(0.0D);
			pics[0].setFitHeight(box);
			pics[0].setFitWidth(width / 6D);
			pics[1].setLayoutX(marginX);
			pics[1].setLayoutY(marginY);
			pics[1].setFitHeight(box);
			pics[1].setFitWidth(width / 6D);
			pics[2].setLayoutX(box / 4D);
			pics[2].setLayoutY(2D * marginY);
			pics[2].setFitHeight(2D * box);
			pics[2].setFitWidth(width / 3D);
			pics[3].setLayoutX(marginX);
			pics[3].setLayoutY(4D * marginY);
			pics[3].setFitHeight(box);
			pics[3].setFitWidth(width / 6D);
			pics[4].setLayoutX(marginX);
			pics[4].setLayoutY(5D * marginY);
			pics[4].setFitHeight(box);
			pics[4].setFitWidth(width / 6D);
			icon.setLayoutX(width / 3D);
			icon.setLayoutY(height / 3D);
			icon.setFitHeight((2D * height) / 3D);
			icon.setFitWidth(width / 2D);
		}
		Volume.getInstance().update();
	}

	public void exit() {
		stopPlayer();
		playSound();
		Main.getScene().setRoot(Main.getMenu());
		Main.getMenu().update();
	}
	
	private void stopPlayer() {
		if(player!=null) {
			player.stop();
		}
		player=null;
	}

	private PMFPlayer player;
	private Background bg;
	private HBox status;
	private Clock clock;
	private Battery battery;
	private ImageView pointer;
	private ImageView pics[];
	private ImageView icon;
	private File files[];
	private FileFilter filter;

}
