package sony;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;
    private static Scene scene;
    private static XMB menu = new XMB();
    private static boolean usb = false;

	public static void main(String args[]) {
		launch(args);
	}

	public static Scene getScene() {
		if (scene == null)
			scene = new Scene(new Intro(), primaryStage.getWidth(), primaryStage.getHeight());
		return scene;
	}

	public static XMB getMenu() {
		return menu;
	}

	public static void fullscreen(boolean value) {
		if (primaryStage != null)
			primaryStage.setFullScreen(value);
	}

	public static boolean isFullScreen() {
		return primaryStage.isFullScreen();
	}

	public static boolean isVisible() {
		return primaryStage.isShowing();
	}

	public static void setUsb(boolean value) {
		usb = value;
	}

	public static boolean isUSB() {
		return usb;
	}

	public static String getValueOf(String member) {
		String result = "";
		String s = member;
		byte byte0 = -1;
		switch (s.hashCode()) {
		case 116100:
			if (s.equals("usb"))
				byte0 = 0;
			break;

		case 110066619:
			if (s.equals("fullscreen"))
				byte0 = 1;
			break;
		}
		switch (byte0) {
		case 0: // '\0'
			result = isUSB() ? "ON" : "OFF";
			break;

		case 1: // '\001'
			result = isFullScreen() ? "ON" : "OFF";
			break;
		}
		return result;
	}

	public static String getUserHome()
    {
        if(isUSB())
        {
            String usbDrive = USB.getUsbDrive();
            if(!usbDrive.equals(""))
                return usbDrive;
        }
        return System.getProperty("user.home");
    }

	@Override
	public void start(Stage primaryStage)
        throws Exception
    {
		Main.primaryStage = primaryStage;
		Main.primaryStage.setTitle("Sony Playstation");
		Main.primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/sony/img/playstation-256.png")));
		Main.primaryStage.setFullScreen(true);
		Main.primaryStage.setScene(getScene());
        getScene().widthProperty().addListener(new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                Base root = (Base)Main.getScene().getRoot();
                root.update();
            }
        });
        getScene().heightProperty().addListener(new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                Base root = (Base)Main.getScene().getRoot();
                root.update();
            }
        });
        getScene().addEventFilter(KeyEvent.KEY_PRESSED, new Controller());
        primaryStage.show();
        SystemTray.createSystemTray(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        SystemTray.exit();
        super.stop();
        System.exit(0);
    }

}
