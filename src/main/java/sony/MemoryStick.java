package sony;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import sony.xmb.Background;
import sony.xmb.Battery;
import sony.xmb.Clock;
import sony.xmb.Volume;

public class MemoryStick extends Pane implements Base {
	private class Item extends Pane {

		private void setMetaData(File file) {
			String list[] = file.getParentFile().list(filter);
			int nbr = getPosition(file, list);
			if (file.getName().toUpperCase().endsWith("MP3"))
				extension.setImage(
						new Image(getClass().getResourceAsStream("/sony/img/mp3.png"), 80D, 80D, false, false));
			else if (file.getName().toUpperCase().endsWith("WAV"))
				extension.setImage(
						new Image(getClass().getResourceAsStream("/sony/img/wav.png"), 80D, 80D, false, false));
			else if (file.getName().toUpperCase().endsWith("JPG") || file.getName().toUpperCase().endsWith("JPEG"))
				extension.setImage(
						new Image(getClass().getResourceAsStream("/sony/img/jpg.png"), 80D, 80D, false, false));
			else if (file.getName().toUpperCase().endsWith("JPG"))
				extension.setImage(
						new Image(getClass().getResourceAsStream("/sony/img/png.png"), 80D, 80D, false, false));
			else if (file.getName().toUpperCase().endsWith("PNG"))
				extension.setImage(
						new Image(getClass().getResourceAsStream("/sony/img/gif.png"), 80D, 80D, false, false));
			else if (file.getName().toUpperCase().endsWith("MP4"))
				extension.setImage(
						new Image(getClass().getResourceAsStream("/sony/img/mp4.png"), 80D, 80D, false, false));
			else if (file.getName().toUpperCase().endsWith("FLV"))
				extension.setImage(
						new Image(getClass().getResourceAsStream("/sony/img/flv.png"), 80D, 80D, false, false));
			else
				extension.setImage(null);
			if (nbr == 0) {
				number.setText((new StringBuilder()).append(nbr).append("").toString());
			}
			if (category.getTitle().toUpperCase().equals("MUSIC")) {
				try {
					MP3File mp3 = new MP3File(file);
					Tag tag = mp3.getTag();
					title.setText(tag.getFirst(FieldKey.TITLE));
					artist.setText(tag.getFirst(FieldKey.ARTIST));
					Artwork image = tag.getFirstArtwork();
					if (image != null)
						artwork.setImage(
								new Image(new ByteArrayInputStream(image.getBinaryData()), 200D, 200D, false, false));
					else
						artwork.setImage(null);
				} catch (Exception ex) {
					title.setText("");
					artist.setText("");
					artwork.setImage(null);
				}
			} else if (category.getTitle().toUpperCase().equals("PHOTO")) {
				title.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
				artist.setText("");
				try {
					artwork.setImage(new Image(new FileInputStream(file), 200D, 200D, false, false));
				} catch (FileNotFoundException ex) {
					artwork.setImage(null);
				}
			} else if (category.getTitle().toUpperCase().equals("VIDEO")) {
				title.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
				artist.setText("");
				artwork.setImage(null);
			} else {
				title.setText("");
				artist.setText("");
			}
			if (title.getText().isEmpty())
				title.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
		}

		public void update() {
			if (files[index] == null) {
				setVisible(false);
			} else {
				setVisible(true);
				setMetaData(files[index]);
				Scene scene = getScene();
				if (scene != null) {
					double box = getScene().getWidth() / 7D;
					rectangle.setWidth(box / 2D);
					rectangle.setHeight(box / 2D);
					rectangle.setLayoutX(1.5D * box);
					rectangle.setLayoutY((1.25D + (double) index * 0.59999999999999998D) * box);
					number.setMinWidth(box / 2D);
					number.setMinHeight(box / 2D);
					number.setLayoutX(1.5D * box);
					number.setLayoutY((1.25D + (double) index * 0.59999999999999998D) * box);
					number.setFont(new Font(box / 5D));
					title.setLayoutX(2.25D * box);
					title.setLayoutY((1.25D + (double) index * 0.59999999999999998D) * box);
					title.setFont(new Font(box / 5D));
					line.setWidth(scene.getWidth() - 2.3999999999999999D * box);
					line.setHeight(2D);
					line.setLayoutX(2.25D * box);
					line.setLayoutY((1.5D + (double) index * 0.59999999999999998D) * box);
					artist.setLayoutX(2.25D * box);
					artist.setLayoutY((1.5D + (double) index * 0.59999999999999998D) * box);
					artist.setFont(new Font(box / 5D));
					extension.setLayoutX(scene.getWidth() - (17D * box) / 30D);
					extension.setLayoutY((1.5D + (double) index * 0.59999999999999998D) * box);
					extension.setFitHeight(box / 5D);
					extension.setFitWidth(box / 2D);
					artwork.setFitWidth(box / 2D);
					artwork.setFitHeight(box / 2D);
					artwork.setLayoutX(1.5D * box);
					artwork.setLayoutY((1.25D + (double) index * 0.59999999999999998D) * box);
				}
			}
		}

		private int index;
		private Rectangle rectangle;
		private Label number;
		private Label title;
		private Label artist;
		private Rectangle line;
		private ImageView extension;
		private ImageView artwork;

		public Item(int index) {
			super();
			this.index = index;
			rectangle = new Rectangle();
			number = new Label();
			title = new Label();
			artist = new Label();
			line = new Rectangle();
			extension = new ImageView();
			artwork = new ImageView();
			this.index = index;
			if (index == 1) {
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

					private boolean direction=true;

				});
				Timeline repeat = new Timeline(kf);
				repeat.setCycleCount(Timeline.INDEFINITE);
				repeat.play();
				rectangle.setEffect(dropShadow);
			}
			rectangle.setArcHeight(25D);
			rectangle.setArcWidth(25D);
			rectangle.setFill(null);
			rectangle.setStroke(Color.WHITE);
			rectangle.setStrokeWidth(5D);
			getChildren().add(rectangle);
			number.setAlignment(Pos.CENTER);
			number.setTextFill(Color.WHITE);
			number.setTextAlignment(TextAlignment.CENTER);
			getChildren().add(number);
			title.setAlignment(Pos.CENTER_LEFT);
			title.setTextFill(Color.WHITE);
			getChildren().add(title);
			line.setFill(Color.WHITE);
			line.setArcHeight(5D);
			line.setArcWidth(5D);
			line.setStroke(Color.WHITE);
			line.setStrokeWidth(2D);
			line.setVisible(index == 1);
			getChildren().add(line);
			artist.setAlignment(Pos.CENTER_LEFT);
			artist.setTextFill(Color.WHITE);
			getChildren().add(artist);
			extension.setVisible(index == 1);
			getChildren().add(extension);
			getChildren().add(artwork);
		}
	}

	public MemoryStick(final sony.xmb.Config.Category category) {
		bg = new Background();
		status = new HBox(8D);
		clock = new Clock();
		battery = new Battery();
		categoryIcon = new ImageView();
		memoryIcon = new ImageView();
		selected = new Label();
		pointer = new ImageView();
		files = new File[4];
		item0 = new Item(0);
		item1 = new Item(1);
		item2 = new Item(2);
		item3 = new Item(3);
		this.category = category;
		getChildren().add(bg);
		status.getChildren().add(clock);
		status.getChildren().add(battery);
		getChildren().add(status);
		categoryIcon.setImage(new Image(getClass().getResourceAsStream(category.getImage())));
		DropShadow effect = new DropShadow();
		effect.setOffsetX(0.0D);
		effect.setOffsetY(0.0D);
		effect.setSpread(0.5D);
		effect.setColor(Color.WHITE);
		categoryIcon.setEffect(effect);
		getChildren().add(categoryIcon);
		selected.setText(category.getTitle());
		selected.setTextFill(Color.WHITE);
		selected.setAlignment(Pos.CENTER);
		getChildren().add(selected);
		memoryIcon.setImage(new Image(getClass().getResourceAsStream("/sony/img/memorystick.png")));
		memoryIcon.setEffect(effect);
		getChildren().add(memoryIcon);
		pointer.setImage(new Image(getClass().getResourceAsStream("/sony/img/pointer.png")));
		pointer.setEffect(effect);
		getChildren().add(pointer);
		item0.setOpacity(0.5D);
		getChildren().add(item0);
		getChildren().add(item1);
		item2.setOpacity(0.5D);
		getChildren().add(item2);
		item3.setOpacity(0.5D);
		getChildren().add(item3);
		getChildren().add(Volume.getInstance());
		filter = new FilenameFilter() {

			public boolean accept(File dir, String file) {
				if (category.getTitle().toUpperCase().equals("MUSIC"))
					return file.toUpperCase().endsWith("MP3") || file.toUpperCase().endsWith("WAV");
				if (category.getTitle().toUpperCase().equals("PHOTO"))
					return file.toUpperCase().endsWith("JPG") || file.toUpperCase().endsWith("PNG")
							|| file.toUpperCase().endsWith("GIF")
							|| file.toUpperCase().endsWith("JPEG");
				if (category.getTitle().toUpperCase().equals("VIDEO"))
					return file.toUpperCase().endsWith("MP4") || file.toUpperCase().endsWith("FLV");
				if (category.getTitle().toUpperCase().equals("GAME"))
					return file.toUpperCase().endsWith("ISO")|| file.toUpperCase().endsWith("CSO");
				else
					return false;
			}
		};
		File currentDir = new File(Main.getUserHome()+"/Sony/"+category.getTitle());
		if (!currentDir.exists())
			currentDir.mkdirs();
		files[0] = null;
		String dir[] = currentDir.list(filter);
		files[1] = dir.length <= 0 ? null : new File(Main.getUserHome()+"/Sony/"+category.getTitle()+"/"+dir[0]);
		files[2] = dir.length <= 1 ? null : new File(Main.getUserHome()+"/Sony/"+category.getTitle()+"/"+dir[1]);
		files[3] = dir.length <= 2 ? null : new File(Main.getUserHome()+"/Sony/"+category.getTitle()+"/"+dir[2]);
	}

	private int getPosition(File file, String list[]) {
		int nbr = 0;
		int i = 0;
		do {
			if (i >= list.length)
				break;
			if (list[i].equals(file.getName())) {
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

	public void setFile(File file) {
		files[1] = file;
		String list[] = file.getParentFile().list(filter);
		int index = getPosition(files[1], list) - 1;
		files[0] = list.length <= index - 1 || index - 1 < 0 ? null : new File(Main.getUserHome()+"/Sony/"+category.getTitle()+"/"+list[index - 1]);
		files[2] = list.length <= index + 1 || index + 1 < 0 ? null : new File(Main.getUserHome()+"/Sony/"+category.getTitle()+"/"+list[index + 1]);
		files[3] = list.length <= index + 2 || index + 2 < 0 ? null : new File(Main.getUserHome()+"/Sony/"+category.getTitle()+"/"+list[index + 2]);
	}

	public void left() {
		exit();
		playSound();
	}

	public void right() {
		enter();
		playSound();
	}

	public void up() {
		if (files[0] != null)
			setFile(files[0]);
		playSound();
	}

	public void down() {
		if (files[2] != null)
			setFile(files[2]);
		playSound();
	}

	public void enter() {
		try {
			String s = category.getTitle().toUpperCase();
			switch (s) {
			case "MUSIC":
				Main.getScene().setRoot(new Music(files[1]));
				break;
			case "VIDEO":
				Main.getScene().setRoot(new Video(files[1]));
				break;
			case "PHOTO":
				Main.getScene().setRoot(new Photo(files[1]));
				break;
			case "GAME":
				SystemTray.minimizeToSystemTray();
				String vbsfile = "\""+Utils.getJarFolder()+"emulator_bat.vbs\"";
				String batfile = ("\""+Utils.getJarFolder()+"emulator.bat\"").replace('\\', '/');
				String isofile = ("\""+files[1].getPath()+"\"").replace('\\', '/');
				String emulator = "WScript.exe " + vbsfile + " " + batfile + " " + isofile;
				Runtime.getRuntime().exec(emulator);
				break;
			}
		} catch (Exception ex) {
		}
		((Base) Main.getScene().getRoot()).update();
		playSound();
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
			resize(categoryIcon, 1.0D);
			double box = getScene().getWidth() / 7D;
			selected.setLayoutX(0.0D);
			selected.setLayoutY(1.6D * box);
			resize(selected, 1.0D);
			resize(memoryIcon, 0.6D);
			pointer.setLayoutX(box);
			pointer.setLayoutY(2D * box);
			pointer.setFitHeight(box / 5D);
			pointer.setFitWidth(box / 4D);
		}
		item0.update();
		item1.update();
		item2.update();
		item3.update();
		Volume.getInstance().update();
	}

	public void exit() {
		playSound();
		Main.getScene().setRoot(Main.getMenu());
		Main.getMenu().update();
	}

	private void resize(Label label, double scale) {
		if (getScene() != null) {
			double box = getScene().getWidth() / 7D;
			label.setPrefWidth(scale * box);
			label.setPrefHeight((scale * box) / 5D);
			label.setFont(new Font((scale * box) / 5D));
		}
	}

	private void resize(ImageView img, double scale) {
		if (getScene() != null) {
			double box = getScene().getWidth() / 7D;
			double fit = scale * box;
			img.setLayoutX(scale == 1.0D ? 0.0D : 0.25D * box);
			img.setLayoutY(scale != 1.0D ? 1.85D * box : 0.75D * box);
			img.setFitHeight(fit);
			img.setFitWidth(fit);
		}
	}

	private sony.xmb.Config.Category category;
	private Background bg;
	private HBox status;
	private Clock clock;
	private Battery battery;
	private ImageView categoryIcon;
	private ImageView memoryIcon;
	private Label selected;
	private ImageView pointer;
	private File files[];
	private Item item0;
	private Item item1;
	private Item item2;
	private Item item3;
	private FilenameFilter filter;

}
