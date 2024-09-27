package sony;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import sony.xmb.Background;
import sony.xmb.Battery;
import sony.xmb.Clock;
import sony.xmb.Volume;

public class Browser extends Pane implements Base {

	public Browser() {
		bg = new Background();
		status = new HBox(8D);
		clock = new Clock();
		battery = new Battery();
		web = new WebView();
		webEngine = web.getEngine();
		address = new TextField("http://www.google.com");
		address.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if(KeyCode.ENTER.equals(ke.getCode())) {
					webEngine.load(address.getText());
				}
			}
		});
		image = new Image(getClass().getResourceAsStream("/sony/img/update.png"));
		view = new ImageView(image);
		refresh = new Button("", view);
		getChildren().add(bg);
		status.getChildren().add(clock);
		status.getChildren().add(battery);
		getChildren().add(address);
		refresh.setCursor(Cursor.HAND);
		refresh.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				webEngine.load(address.getText());
			}
		});
		refresh.setStyle("-fx-text-fill: black;-fx-background-color: white;");
		getChildren().add(refresh);
		getChildren().add(status);
		getChildren().add(web);
		getChildren().add(Volume.getInstance());
		webEngine.load(address.getText());
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
			address.setLayoutX(80D);
			address.setLayoutY(80D);
			address.setPrefHeight(40D);
			address.setPrefWidth(scene.getWidth() - 240D);
			refresh.setLayoutX(scene.getWidth() - 140D);
			refresh.setLayoutY(80D);
			refresh.setPrefHeight(40D);
			refresh.setPrefWidth(40D);
			view.setFitHeight(35D);
			view.setFitWidth(35D);
			web.setLayoutX(80D);
			web.setLayoutY(160D);
			web.setPrefHeight(scene.getHeight() - 200D);
			web.setPrefWidth(scene.getWidth() - 160D);
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
	private WebView web;
	private WebEngine webEngine;
	private TextField address;
	private Image image;
	private ImageView view;
	private Button refresh;
}
