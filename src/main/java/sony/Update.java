package sony;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import sony.xmb.Background;
import sony.xmb.Battery;
import sony.xmb.Clock;
import sony.xmb.Volume;

public class Update extends Pane implements Base {
	
	public Update() {
		bg = new Background();
		status = new HBox(8D);
		clock = new Clock();
		battery = new Battery();
		getChildren().add(bg);
		status.getChildren().add(clock);
		status.getChildren().add(battery);
		getChildren().add(status);
		getChildren().add(Volume.getInstance());
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
			Volume.getInstance().update();
		}
		
	}

	public void exit() {
		Main.getScene().setRoot(Main.getMenu());
		Main.getMenu().update();
	}

	private Background bg;
	private HBox status;
	private Clock clock;
	private Battery battery;
	
}
