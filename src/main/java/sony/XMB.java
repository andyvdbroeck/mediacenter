package sony;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Duration;
import sony.xmb.Background;
import sony.xmb.Battery;
import sony.xmb.Clock;
import sony.xmb.Config;
import sony.xmb.Config.Category;
import sony.xmb.Config.Item;
import sony.xmb.Menu;
import sony.xmb.Volume;

public class XMB extends Pane implements Base {

	public XMB() {
		bg = new Background();
		bg.setImage(new Image(getClass().getResourceAsStream("/sony/img/wave.gif")));
		status = new HBox(8D);
		clock = new Clock();
		battery = new Battery();
		menu = new Menu();
		m = new Media(sony.Controller.class.getResource("/sony/snd/move.mp3").toString());
		mp = new MediaPlayer(m);
		getChildren().add(bg);
		status.getChildren().add(clock);
		status.getChildren().add(battery);
		getChildren().add(status);
		getChildren().add(menu);
		getChildren().add(Volume.getInstance());
	}

	public void setTheme(Image theme) {
		bg.setImage(theme);
	}

	public Image getTheme() {
		return bg.getImage();
	}

	private void play() {
		mp.setVolume(Volume.getInstance().getVolume());
		mp.seek(Duration.ONE);
		mp.play();
	}

	public void left() {
		if (selectedCategory != 0)
			selectedCategory--;
		play();
	}

	public void right() {
		if (selectedCategory != list.size() - 1)
			selectedCategory++;
		play();
	}

	public void up() {
		sony.xmb.Config.Category category = (sony.xmb.Config.Category) list.get(selectedCategory);
		int selectedItem = category.getSelected();
		if (selectedItem != 0) {
			selectedItem--;
			category.setSelected(selectedItem);
		}
		play();
	}

	public void down() {
		sony.xmb.Config.Category category = (sony.xmb.Config.Category) list.get(selectedCategory);
		int selectedItem = category.getSelected();
		int itemCount = category.getItems().size();
		if (selectedItem != itemCount - 1) {
			selectedItem++;
			category.setSelected(selectedItem);
		}
		play();
	}

	public void enter() {
		Category category = list.get(selectedCategory);
		Item selectedItem = category.getItem(category.getSelected());
		switch (selectedItem.getTitle()) {
		case "System update":
			Update update = new Update();
			getScene().setRoot(update);
			update.update();
			break;
			
		case "Camera":
			Camera camera = new Camera();
			getScene().setRoot(camera);
			camera.update();
			break;

		case "Memory Stick":
			MemoryStick memory = new MemoryStick(category);
			getScene().setRoot(memory);
			memory.update();
			break;

		case "Theme Settings":
			Theme theme = new Theme();
			getScene().setRoot(theme);
			theme.update();
			break;

		case "UMD":
			UMD video = new UMD();
			getScene().setRoot(video);
			video.update();
			break;

		case "PSP":
			PSP psp = new PSP();
			getScene().setRoot(psp);
			psp.update();
			break;

		case "Radio":
			Radio radio = new Radio();
			getScene().setRoot(radio);
			radio.update();
			break;

		case "WWW Browser":
			Browser browser = new Browser();
			getScene().setRoot(browser);
			browser.update();
			break;

		case "USB Connection":
			if (Main.isUSB()) {
				Main.setUsb(false);
				break;
			}
			String usbDrive = USB.getUsbDrive();
			if (!usbDrive.equals(""))
				Main.setUsb(true);
			break;
		}
		update();
		play();
	}

	public void exit() {
		System.exit(0);
	}

	public void update() {
		if (!getChildren().contains(Volume.getInstance()))
			getChildren().add(Volume.getInstance());
		mp.setVolume(Volume.getInstance().getVolume());
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
			menu.update();
			Volume.getInstance().update();
		}
	}

	public static List<Category> list = Config.getMenu();
	public static int selectedCategory = 0;
	private Background bg;
	private HBox status;
	private Clock clock;
	private Battery battery;
	private Menu menu;
	private Media m;
	private MediaPlayer mp;

}
