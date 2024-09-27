package sony;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import sony.xmb.Config;
import sony.xmb.Volume;

public class Photo extends Pane implements Base {

	public Photo(File file) {
		image = new ImageView();
		this.file = file;
		setStyle("-fx-background-color: white;");
		getChildren().add(image);
		getChildren().add(Volume.getInstance());
	}

	public void right() {
		File list[] = file.getParentFile().listFiles();
		int i = 0;
		do {
			if (i >= list.length)
				break;
			if (list[i].getName().equals(file.getName()) && i + 1 < list.length) {
				file = list[i + 1];
				break;
			}
			i++;
		} while (true);
	}

	public void left() {
		File list[] = file.getParentFile().listFiles();
		int i = 0;
		do {
			if (i >= list.length)
				break;
			if (list[i].getName().equals(file.getName()) && i - 1 >= 0) {
				file = list[i - 1];
				break;
			}
			i++;
		} while (true);
	}

	public void up() {
		Volume.getInstance().higher();
	}

	public void down() {
		Volume.getInstance().lower();
	}

	public void enter() {
	}

	public void update() {
		if (!getChildren().contains(Volume.getInstance()))
			getChildren().add(Volume.getInstance());
		if (getScene() != null) {
			try {
				BufferedImage img = ImageIO.read(file);
				double width = img.getTileWidth();
				double height = img.getTileHeight();
				if (width < height) {
					width = (width / height) * getScene().getHeight();
					height = getScene().getHeight();
				} else {
					height = (height / width) * getScene().getWidth();
					width = getScene().getWidth();
				}
				image.setImage(new Image(new FileInputStream(file), width, height, false, false));
				image.setLayoutX((getScene().getWidth() - width) / 2D);
				image.setLayoutY((getScene().getHeight() - height) / 2D);
				image.setFitHeight(height);
				image.setFitWidth(width);
			} catch (Exception ex) {
			}
			Volume.getInstance().update();
		}
	}

	public void exit() {
		MemoryStick memory = new MemoryStick(Config.getCategory("Photo"));
		memory.setFile(file);
		Main.getScene().setRoot(memory);
		memory.update();
	}

	private File file;
	private ImageView image;
}
