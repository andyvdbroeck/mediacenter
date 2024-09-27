package sony;

import java.io.File;
import java.util.Iterator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import sony.xmb.Background;
import sony.xmb.Battery;
import sony.xmb.Clock;
import sony.xmb.Volume;

public class Theme extends Pane implements Base {
	private class AnimatedGif extends ImageView {

		public void update() {
			File list[] = dir.listFiles();
			if (index + position < list.length) {
				if (position == 0 && index == 0)
					setImage(new Image(getClass().getResourceAsStream("/sony/img/wave.gif")));
				else
					setImage(new Image(list[(position + index) - 1].toURI().toASCIIString()));
			} else {
				setImage(null);
			}
			if (getScene() != null) {
				double box = getScene().getWidth() / 14D;
				setFitHeight(box);
				setFitWidth(box);
				setLayoutX(4D * box + (double) (index * 2) * box);
				setLayoutY(getHeight() - 2D * box);
			}
		}

		private int index;

		public AnimatedGif(int index) {
			super();
			this.index = index;
			if (index == 0) {
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
				setEffect(dropShadow);
			}
		}
	}

	public Theme() {
		position = 0;
		bg = new Background();
		status = new HBox(8D);
		clock = new Clock();
		battery = new Battery();
		dir = new File((new StringBuilder()).append(Main.getUserHome()).append("/Sony/Theme").toString());
		if(!dir.exists()) {
			dir.mkdirs();
		}
		m = new Media(sony.Controller.class.getResource("/sony/snd/move.mp3").toString());
		mp = new MediaPlayer(m);
		getChildren().add(bg);
		status.getChildren().add(clock);
		status.getChildren().add(battery);
		getChildren().add(status);
		getChildren().add(new AnimatedGif(0));
		getChildren().add(new AnimatedGif(1));
		getChildren().add(new AnimatedGif(2));
		getChildren().add(new AnimatedGif(3));
		getChildren().add(new AnimatedGif(4));
		getChildren().add(Volume.getInstance());
	}

	private void play() {
		mp.setVolume(Volume.getInstance().getVolume());
		mp.seek(Duration.ONE);
		mp.play();
	}

	public void left() {
		if (position > 0)
			position--;
		play();
	}

	public void right() {
		if (position + 1 < dir.list().length)
			position++;
		play();
	}

	public void up() {
	}

	public void down() {
	}

	public void enter() {
		File list[] = dir.listFiles();
		if (position == 0)
			Main.getMenu().setTheme(new Image(getClass().getResourceAsStream("/sony/img/wave.gif")));
		else
			Main.getMenu().setTheme(new Image(list[position - 1].toURI().toASCIIString()));
		play();
		exit();
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
			Iterator<Node> i$ = getChildren().iterator();
			do {
				if (!i$.hasNext())
					break;
				Node gif = (Node) i$.next();
				if (gif instanceof AnimatedGif)
					((AnimatedGif) gif).update();
			} while (true);
			Volume.getInstance().update();
		}
		File list[] = dir.listFiles();
		if (position == 0)
			bg.setImage(new Image(getClass().getResourceAsStream("/sony/img/wave.gif")));
		else
			bg.setImage(new Image(list[position - 1].toURI().toASCIIString()));
	}

	public void exit() {
		Main.getScene().setRoot(Main.getMenu());
		Main.getMenu().update();
	}

	private int position;
	private Background bg;
	private HBox status;
	private Clock clock;
	private Battery battery;
	private File dir;
	private Media m;
	private MediaPlayer mp;

}
