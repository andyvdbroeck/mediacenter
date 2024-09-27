package sony;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sony.xmb.Volume;

public class Camera extends Pane implements Base {

	public Camera() {
		camera = true;
		current = null;
		dir = new File((new StringBuilder()).append(Main.getUserHome()).append("/Sony/Camera").toString());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		view = new ImageView();
		getChildren().add(view);
		getChildren().add(Volume.getInstance());
		webcam.open();
		new Thread() {
			public void run() {
				while (camera) {
					renderImage();
				}
			}
		}.start();
	}

	public void update() {
		if (!getChildren().contains(Volume.getInstance()))
			getChildren().add(Volume.getInstance());
		if (getScene() != null) {
			view.setLayoutX(0.0D);
			view.setLayoutY(0.0D);
			view.setFitHeight(getScene().getHeight());
			view.setFitWidth(getScene().getWidth());
			setWidth(getScene().getWidth());
			setHeight(getScene().getHeight());
			Volume.getInstance().update();
		}
	}

	private void renderImage() {
		try {
			BufferedImage originalImage = webcam.getImage();
			if (camera) {
				view.setImage(bufferedImageToFXImage(originalImage));
			}

		} catch (IOException ioe) {
		}
	}

	private Image bufferedImageToFXImage(java.awt.Image image) throws IOException {
		if (image != null) {
			if (!(image instanceof RenderedImage)) {
				BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
				Graphics g = bufferedImage.createGraphics();
				g.drawImage(image, 0, 0, null);
				g.dispose();
				image = bufferedImage;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write((RenderedImage) image, "png", out);
			out.flush();
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			return new Image(in);
		} else {
			return null;
		}
	}

	public void left() {
		camera = false;
		if (current == null)
			down();
		if (current != null) {
			File list[] = current.getParentFile().listFiles();
			if (index - 1 >=0) {
				current = list[index - 1];
				index--;
				try {
					view.setImage(bufferedImageToFXImage(ImageIO.read(current)));
				} catch (IOException ex) {}
			}
		}
	}

	public void right() {
		camera = false;
		if (current == null)
			down();
		if (current != null) {
			File list[] = current.getParentFile().listFiles();
			if (index + 1 < list.length) {
				current = list[index + 1];
				index++;
				try {
					view.setImage(bufferedImageToFXImage(ImageIO.read(current)));
				} catch (IOException ex) {}
			}
		}
	}

	public void up() {
		camera = true;
		new Thread() {
			public void run() {
				while (camera) {
					renderImage();
				}
			}
		}.start();
	}

	public void down() {
		File list[] = dir.listFiles();
		if (list.length > 0) {
			camera = false;
			current = list[list.length - 1];
			index = list.length - 1;
			try {
				view.setImage(bufferedImageToFXImage(ImageIO.read(current)));
			} catch (IOException ex) {
			}
		}
	}

	public void enter() {
		try {
			BufferedImage originalImage = webcam.getImage();
			File file = new File((new StringBuilder()).append(Main.getUserHome()).append("/Sony/Camera/")
					.append((new Date()).getTime()).append(".jpg").toString());
			ImageIO.write(originalImage, "jpg", file);
		} catch (IOException ioe) {
		}
	}

	public void exit() {
		webcam.close();
		Main.getScene().setRoot(Main.getMenu());
		Main.getMenu().update();
	}

	private boolean camera;
	private File current;
	private int index=0;
	private File dir;
	private ImageView view;
	private Webcam webcam = Webcam.getDefault();
}
