package sony;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sony.xmb.Volume;

public class Controller implements EventHandler<KeyEvent> {

	public void handle(KeyEvent event) {
		if (Main.getScene() != null) {
			Base root = (Base) Main.getScene().getRoot();
			if (KeyCode.UP.equals(event.getCode()))
				root.up();
			if (KeyCode.DOWN.equals(event.getCode()))
				root.down();
			if (KeyCode.LEFT.equals(event.getCode()))
				root.left();
			if (KeyCode.RIGHT.equals(event.getCode()))
				root.right();
			if (KeyCode.ENTER.equals(event.getCode()))
				root.enter();
			if (KeyCode.TAB.equals(event.getCode()))
				root.exit();
			if (KeyCode.ESCAPE.equals(event.getCode()))
				Main.fullscreen(false);
			if (KeyCode.F11.equals(event.getCode()))
				Main.fullscreen(true);
			if (KeyCode.F12.equals(event.getCode()))
				SystemTray.minimizeToSystemTray();
			if (KeyCode.PAGE_UP.equals(event.getCode()))
				Volume.getInstance().higher();
			if (KeyCode.PAGE_DOWN.equals(event.getCode()))
				Volume.getInstance().lower();
			root.update();
		}
	}
}
