package sony.xmb;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Battery extends ImageView {

	public Battery() {
		status();
		KeyFrame kf = new KeyFrame(Duration.minutes(1.0D), new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				status();
			}
		});
		Timeline repeat = new Timeline( kf );
		repeat.setCycleCount(Timeline.INDEFINITE);
		repeat.play();
	}

	private void status() {
		Power.SYSTEM_POWER_STATUS batteryStatus = new Power.SYSTEM_POWER_STATUS();
		Power.INSTANCE.GetSystemPowerStatus(batteryStatus);
		String percent = batteryStatus.getBatteryLifePercent();
		int status = Integer.parseInt(percent.substring(0,percent.length()-1));
		if(status == 100) { 
			setImage(FULL_BATTERY);
		} else if(status > 66) {
			setImage(F66_BATTERY);
		} else if(status > 33) {
			setImage(F33_BATTERY);
		} else {
			setImage(NO_BATTERY);
		}
	}

	private final Image NO_BATTERY = new Image(getClass().getResourceAsStream("/sony/img/nobattery.png"), 80D, 60D,
			false, false);
	private final Image F33_BATTERY = new Image(getClass().getResourceAsStream("/sony/img/33battery.png"), 80D, 60D,
			false, false);
	private final Image F66_BATTERY = new Image(getClass().getResourceAsStream("/sony/img/66battery.png"), 80D, 60D,
			false, false);
	private final Image FULL_BATTERY = new Image(getClass().getResourceAsStream("/sony/img/battery.png"), 80D, 60D,
			false, false);

}
