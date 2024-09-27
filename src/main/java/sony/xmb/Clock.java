package sony.xmb;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class Clock extends Label
{

    private static String getTime()
    {
        return sdf.format(new Date());
    }

    public Clock()
    {
        setText(getTime());
        setTextFill(Color.WHITE);
        setFont(new Font(40D));
        KeyFrame kf = new KeyFrame(Duration.seconds(1.0D), new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event)
            {
                setText(Clock.getTime());
            }
        });
        Timeline repeat = new Timeline(kf);
        repeat.setCycleCount(Timeline.INDEFINITE);
        repeat.play();
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


}
