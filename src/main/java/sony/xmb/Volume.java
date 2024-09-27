package sony.xmb;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Volume extends Pane {

	public static Volume getInstance() {
		return volume;
	}

	private Volume() {
		value = 1.0D;
		img = new ImageView();
		bar = new Rectangle[20];
		img.setImage(new Image(getClass().getResourceAsStream("/sony/img/volume.png")));
		getChildren().add(img);
		for (int i = 0; i < bar.length; i++) {
			bar[i] = new Rectangle();
			getChildren().add(bar[i]);
		}

		fade();
	}

	public double getVolume() {
		return value;
	}

	public void higher() {
		if (value + 0.05D <= 1.0D)
			value += 0.05D;
		else
			value = 1.0D;
		img.setImage(new Image(getClass().getResourceAsStream("/sony/img/volume.png")));
		fade();
	}

	public void lower() {
		if (value - 0.05D >= 0.0D)
			value -= 0.05D;
		else
			value = 0.0D;
		img.setImage(new Image(getClass().getResourceAsStream("/sony/img/sound.png")));
		fade();
	}

	private void fade() {
		update();
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000D), img);
		fadeTransition.setFromValue(1.0D);
		fadeTransition.setToValue(0.0D);
		fadeTransition.play();
		for (int i = 0; i < bar.length; i++) {
			fadeTransition = new FadeTransition(Duration.millis(2000D), bar[i]);
			fadeTransition.setFromValue(1.0D);
			fadeTransition.setToValue(0.0D);
			fadeTransition.play();
		}

	}

	public void update() {
		if (getScene() != null) {
			double height = getScene().getWidth() / 24D;
			double width = height / 2D;
			img.setFitWidth(height);
			img.setFitHeight(height);
			img.setLayoutX(1.5D * height);
			img.setLayoutY(0.0D);
			for (int i = 0; i < bar.length; i++) {
				bar[i].setWidth(width);
				bar[i].setHeight(height);
				bar[i].setFill(
						(double) ((i + 1) * 5) > value * 100D ? null : ((javafx.scene.paint.Paint) (Color.WHITE)));
				bar[i].setLayoutX(2.5D * height + (double) (2 * i) * width);
				bar[i].setLayoutY(0.0D);
				bar[i].setStroke(Color.GRAY);
				bar[i].setStrokeWidth(3D);
			}

			setLayoutX(0.0D);
			setLayoutY(getScene().getHeight() - getScene().getWidth() / 8D);
			setPrefHeight(getScene().getWidth() / 8D);
			setPrefWidth(getScene().getWidth());
		}
	}

	private static Volume volume = new Volume();
	private double value;
	private ImageView img;
	private Rectangle bar[];

}
