package sony;

import java.io.File;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import sony.xmb.Config;
import sony.xmb.Volume;

public class Video extends Pane implements Base {

	public Video(File file) {
		container = new VBox();
		mediaView = new MediaView();
		play = new ImageView();
		duration = new Rectangle();
		timeline = new Rectangle();
		currentTime = new Label("00:00:01");
		totalTime = new Label();
		this.file = file;
		String style_inner = "-fx-background-color: black;";
		setStyle(style_inner);
		container.setAlignment(Pos.CENTER);
		Media media = new Media(file.toURI().toASCIIString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaView.setMediaPlayer(mediaPlayer);
		mediaView.getMediaPlayer().setOnEndOfMedia(new Runnable() {

			public void run() {
				right();
				update();
			}
		});
		mediaView.setStyle(style_inner);
		container.getChildren().add(mediaView);
		getChildren().add(container);
		play.setImage(new Image(getClass().getResourceAsStream("/sony/img/play.png"), 80D, 60D, false, false));
		getChildren().add(play);
		duration.setFill(Color.WHITE);
		getChildren().add(duration);
		timeline.setFill(Color.BLUE);
		timeline.setArcHeight(5D);
		timeline.setArcWidth(5D);
		timeline.setStroke(Color.BLUE);
		timeline.setStrokeWidth(2D);
		getChildren().add(timeline);
		currentTime.setFont(new Font(40D));
		currentTime.setAlignment(Pos.CENTER_LEFT);
		currentTime.setTextFill(Color.BLUE);
		getChildren().add(currentTime);
		totalTime.setFont(new Font(40D));
		totalTime.setAlignment(Pos.CENTER_RIGHT);
		totalTime.setTextFill(Color.WHITE);
		getChildren().add(totalTime);
		getChildren().add(Volume.getInstance());
		mediaPlayer.play();
		KeyFrame kf = new KeyFrame(Duration.seconds(1.0D), new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				if (mediaView.getMediaPlayer() != null) {
					int hours = (int) mediaView.getMediaPlayer().getCurrentTime().toHours();
					int minutes = (int) mediaView.getMediaPlayer().getCurrentTime().toSeconds() / 60;
					int seconds = (int) mediaView.getMediaPlayer().getCurrentTime().toSeconds() % 60;
					currentTime.setText((new StringBuilder()).append(hours >= 10 ? "" : "0").append(hours).append(":")
							.append(minutes >= 10 ? "" : "0").append(minutes).append(":")
							.append(seconds >= 10 ? "" : "0").append(seconds).toString());
					double pos = 0.0D;
					if (mediaView.getMediaPlayer() != null)
						pos = mediaView.getMediaPlayer().getCurrentTime().toSeconds()
								/ mediaView.getMediaPlayer().getTotalDuration().toSeconds();
					timeline.setWidth(pos * duration.getWidth());
					hours = (int) mediaView.getMediaPlayer().getStopTime().toHours();
					minutes = (int) mediaView.getMediaPlayer().getStopTime().toSeconds() / 60;
					seconds = (int) mediaView.getMediaPlayer().getStopTime().toSeconds() % 60;
					totalTime.setText((new StringBuilder()).append(" / ").append(hours >= 10 ? "" : "0").append(hours)
							.append(":").append(minutes >= 10 ? "" : "0").append(minutes).append(":")
							.append(seconds >= 10 ? "" : "0").append(seconds).toString());
				}
			}
		});
		Timeline repeat = new Timeline( kf );
		repeat.setCycleCount(Timeline.INDEFINITE);
		repeat.play();
		fadeMediaBar();
	}

	private void fadeMediaBar() {
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000D), play);
		fadeTransition.setFromValue(1.0D);
		fadeTransition.setToValue(0.0D);
		fadeTransition.play();
		fadeTransition = new FadeTransition(Duration.millis(1000D), currentTime);
		fadeTransition.setFromValue(1.0D);
		fadeTransition.setToValue(0.0D);
		fadeTransition.play();
		fadeTransition = new FadeTransition(Duration.millis(1000D), totalTime);
		fadeTransition.setFromValue(1.0D);
		fadeTransition.setToValue(0.0D);
		fadeTransition.play();
		fadeTransition = new FadeTransition(Duration.millis(1000D), duration);
		fadeTransition.setFromValue(1.0D);
		fadeTransition.setToValue(0.0D);
		fadeTransition.play();
		fadeTransition = new FadeTransition(Duration.millis(1000D), timeline);
		fadeTransition.setFromValue(1.0D);
		fadeTransition.setToValue(0.0D);
		fadeTransition.play();
	}

	private void showMediaBar() {
		play.setOpacity(1.0D);
		currentTime.setOpacity(1.0D);
		totalTime.setOpacity(1.0D);
		duration.setOpacity(1.0D);
		timeline.setOpacity(1.0D);
	}

	public void right() {
		File list[] = file.getParentFile().listFiles();
		int i = 0;
		do {
			if (i >= list.length)
				break;
			if (list[i].getName().equals(file.getName()) && i + 1 < list.length) {
				file = list[i + 1];
				mediaView.getMediaPlayer().stop();
				Media media = new Media(file.toURI().toASCIIString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				mediaView.setMediaPlayer(mediaPlayer);
				mediaView.getMediaPlayer().setOnEndOfMedia(new Runnable() {

					public void run() {
						right();
						update();
					}
				});
				mediaView.getMediaPlayer().play();
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
				mediaView.getMediaPlayer().stop();
				Media media = new Media(file.toURI().toASCIIString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				mediaView.setMediaPlayer(mediaPlayer);
				mediaView.getMediaPlayer().setOnEndOfMedia(new Runnable() {

					public void run() {
						right();
						update();
					}
				});
				mediaView.getMediaPlayer().play();
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

	public void update() {
		if (!getChildren().contains(Volume.getInstance()))
			getChildren().add(Volume.getInstance());
		mediaView.getMediaPlayer().setVolume(Volume.getInstance().getVolume());
		if (getScene() != null) {
			setWidth(getScene().getWidth());
			setHeight(getScene().getHeight());
			container.setPrefWidth(getScene().getWidth());
			container.setPrefHeight(getScene().getHeight());
			mediaView.setFitWidth(getScene().getWidth());
			mediaView.setFitHeight(getScene().getHeight());
			play.setLayoutX(50D);
			play.setLayoutY(getScene().getHeight() - 90D);
			duration.setWidth(getScene().getWidth() - 225D);
			duration.setHeight(15D);
			duration.setLayoutX(200D);
			duration.setLayoutY(getScene().getHeight() - 65D);
			double pos = 0.0D;
			if (mediaView.getMediaPlayer() != null)
				pos = mediaView.getMediaPlayer().getCurrentTime().toSeconds()
						/ mediaView.getMediaPlayer().getTotalDuration().toSeconds();
			timeline.setWidth(pos * duration.getWidth());
			timeline.setHeight(15D);
			timeline.setLayoutX(200D);
			timeline.setLayoutY(getScene().getHeight() - 65D);
			currentTime.setLayoutX(getScene().getWidth() - 350D);
			currentTime.setLayoutY(getScene().getHeight() - 135D);
			totalTime.setLayoutX(getScene().getWidth() - 200D);
			totalTime.setLayoutY(getScene().getHeight() - 135D);
			Volume.getInstance().update();
		}
	}

	public void enter() {
		if (mediaView.getMediaPlayer() != null) {
			if (javafx.scene.media.MediaPlayer.Status.PLAYING.equals(mediaView.getMediaPlayer().getStatus())) {
				play.setImage(new Image(getClass().getResourceAsStream("/sony/img/pause.png"), 80D, 60D, false, false));
				mediaView.getMediaPlayer().pause();
				showMediaBar();
			} else {
				play.setImage(new Image(getClass().getResourceAsStream("/sony/img/play.png"), 80D, 60D, false, false));
				mediaView.getMediaPlayer().play();
				fadeMediaBar();
			}
		} else {
			play.setImage(new Image(getClass().getResourceAsStream("/sony/img/play.png"), 80D, 60D, false, false));
			mediaView.getMediaPlayer().play();
			fadeMediaBar();
		}
	}

	public void exit() {
		mediaView.getMediaPlayer().stop();
		mediaView.getMediaPlayer().dispose();
		MemoryStick memory = new MemoryStick(Config.getCategory("Video"));
		memory.setFile(file);
		Main.getScene().setRoot(memory);
		memory.update();
	}

	private File file;
	private VBox container;
	private MediaView mediaView;
	private ImageView play;
	private Rectangle duration;
	private Rectangle timeline;
	private Label currentTime;
	private Label totalTime;

}
