package sony;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import sony.xmb.Background;

public class Intro extends Pane implements Base {

	public Intro() {
		Media m = new Media(Intro.class.getResource("/sony/snd/intro.mp3").toString());
		mp = new MediaPlayer(m);
		bg = new Background();
		bg.setImage(new Image(getClass().getResourceAsStream("/sony/img/intro.gif")));
		getChildren().add(bg);
		mp.setOnEndOfMedia(new Runnable() {

			public void run() {
				exit();
			}
		});
		mp.play();
	}

	public void left() {
	}

	public void right() {
	}

	public void up() {
	}

	public void down() {
	}

	public void enter() {
	}

	public void update() {
		Scene scene = getScene();
		if (scene != null) {
			bg.setFitWidth(scene.getWidth());
			bg.setFitHeight(scene.getHeight());
		}
	}

	public void exit() {
		mp.stop();
		Main.getScene().setRoot(Main.getMenu());
		Main.getMenu().update();
	}

	private MediaPlayer mp;
	private Background bg;
}
