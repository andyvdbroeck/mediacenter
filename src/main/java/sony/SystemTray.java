package sony;

import java.awt.AWTException;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;

public class SystemTray {

	public static boolean createSystemTray(Stage stage) {
		if (!java.awt.SystemTray.isSupported())
			return false;
		if (st != null)
			throw new IllegalStateException((new StringBuilder()).append(sony.SystemTray.class.getName())
					.append(" can only be created once").toString());
		if (!Platform.isFxApplicationThread())
			throw new IllegalStateException((new StringBuilder()).append(sony.SystemTray.class.getName())
					.append(" can only be create within the JavaFX application thread").toString());
		SystemTray.stage = stage;
		SystemTray.stage.iconifiedProperty().addListener(new ChangeListener<Boolean>() {

			public void changed(ObservableValue<? extends Boolean> paramObservableValue, Boolean oldValue,
					Boolean newValue) {
				if (newValue.booleanValue())
					SystemTray.minimizeToSystemTray();
				else
					SystemTray.restoreFromSystemTray();
			}

		});
		if (st == null) {
			st = java.awt.SystemTray.isSupported() ? java.awt.SystemTray.getSystemTray() : null;
			if (st != null && st.getTrayIcons().length == 0)
				try {
					java.awt.Image image = ImageIO
							.read(sony.SystemTray.class.getResourceAsStream("/sony/img/playstation16.png"));
					TrayIcon trayIcon = new TrayIcon(image);
					trayIcon.setToolTip("Sony Media Center");
					st.add(trayIcon);
					trayIcon.addMouseListener(new MouseListener() {

						public void mouseReleased(MouseEvent mouseevent) {
						}

						public void mousePressed(MouseEvent mouseevent) {
						}

						public void mouseExited(MouseEvent mouseevent) {
						}

						public void mouseEntered(MouseEvent mouseevent) {
						}

						public void mouseClicked(MouseEvent e) {
							Platform.runLater(new Runnable() {

								public void run() {
									SystemTray.restoreFromSystemTray();
								}
							});
						}

					});
				} catch (IOException e) {
				} catch (AWTException e) {
				}
		}
		return true;
	}

	public static void minimizeToSystemTray() {
		if (dummyPopup == null) {
			dummyPopup = new Stage();
			dummyPopup.initModality(Modality.NONE);
			dummyPopup.initStyle(StageStyle.UTILITY);
			dummyPopup.setWidth(0.0D);
			dummyPopup.setHeight(0.0D);
			dummyPopup.setOpacity(0.0D);
			Scene dummyScene = new Scene(new Group(), 10D, 10D, Color.TRANSPARENT);
			dummyScene.setFill(null);
			dummyPopup.show();
		}
		stage.hide();
	}

	public static void restoreFromSystemTray() {
		stage.show();
		stage.toFront();
		if (dummyPopup != null) {
			dummyPopup.close();
			dummyPopup = null;
		}
	}

	public static void exit() {
		if (st != null) {
			TrayIcon arr$[] = st.getTrayIcons();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++) {
				TrayIcon trayIcon = arr$[i$];
				try {
					st.remove(trayIcon);
				} catch (Throwable t2) {
				}
			}

		}
	}

	private static Stage stage;
	private static Stage dummyPopup;
	private static java.awt.SystemTray st;
}
