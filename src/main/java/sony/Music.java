package sony;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import sony.xmb.Background;
import sony.xmb.Config;
import sony.xmb.Volume;

public class Music extends Pane
    implements Base
{

    public Music(File file)
        throws Exception
    {
        bg = new Background();
        status = new HBox(2D);
        logo = new ImageView();
        position = new Label();
        rectangle = new Rectangle();
        number = new Label();
        title = new Label();
        line = new Rectangle();
        artist = new Label();
        extension = new ImageView();
        play = new ImageView();
        duration = new Rectangle();
        timeline = new Rectangle();
        currentTime = new Label("00:01");
        totalTime = new Label();
        artwork = new ImageView();
        this.file = file;
        init();
        setMetaData(file);
        play();
        KeyFrame kf = new KeyFrame(Duration.seconds(1.0D), new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event)
            {
                if(mp != null)
                {
                    int minutes = (int)mp.getCurrentTime().toSeconds() / 60;
                    int seconds = (int)mp.getCurrentTime().toSeconds() % 60;
                    currentTime.setText((new StringBuilder()).append(minutes >= 10 ? "" : "0").append(minutes).append(":").append(seconds >= 10 ? "" : "0").append(seconds).toString());
                    double pos = 0.0D;
                    if(mp != null)
                        pos = mp.getCurrentTime().toSeconds() / mp.getTotalDuration().toSeconds();
                    timeline.setWidth(pos * duration.getWidth());
                }
            }
        });
        Timeline repeat = new Timeline(kf);
        repeat.setCycleCount(Timeline.INDEFINITE);
        repeat.play();
    }

    private void init()
    {
        getChildren().add(bg);
        getChildren().add(artwork);
        logo.setImage(new Image(getClass().getResourceAsStream("/sony/img/music.png"), 30D, 30D, false, false));
        status.setStyle("-fx-background-color: blue;");
        status.getChildren().add(logo);
        position.setFont(new Font(30D));
        position.setAlignment(Pos.CENTER_RIGHT);
        position.setTextFill(Color.WHITE);
        status.getChildren().add(position);
        getChildren().add(status);
        rectangle.setArcHeight(25D);
        rectangle.setArcWidth(25D);
        rectangle.setFill(null);
        rectangle.setStroke(Color.WHITE);
        rectangle.setStrokeWidth(5D);
        getChildren().add(rectangle);
        number.setFont(new Font(60D));
        number.setMinWidth(100D);
        number.setAlignment(Pos.CENTER);
        number.setTextFill(Color.WHITE);
        getChildren().add(number);
        title.setFont(new Font(40D));
        title.setAlignment(Pos.CENTER_LEFT);
        title.setTextFill(Color.WHITE);
        getChildren().add(title);
        line.setFill(Color.WHITE);
        line.setArcHeight(5D);
        line.setArcWidth(5D);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(2D);
        getChildren().add(line);
        artist.setFont(new Font(40D));
        artist.setAlignment(Pos.CENTER_LEFT);
        artist.setTextFill(Color.WHITE);
        getChildren().add(artist);
        getChildren().add(extension);
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
    }

    public void update()
    {
        if(!getChildren().contains(Volume.getInstance()))
            getChildren().add(Volume.getInstance());
        mp.setVolume(Volume.getInstance().getVolume());
        Scene scene = getScene();
        if(scene != null)
        {
            bg.setFitWidth(scene.getWidth());
            bg.setFitHeight(scene.getHeight());
            position.setPrefWidth(scene.getWidth() - 40D);
            status.setLayoutX(0.0D);
            status.setLayoutY(0.0D);
            status.setPrefWidth(scene.getWidth());
            status.setPrefHeight(30D);
            status.setAlignment(Pos.CENTER_LEFT);
            rectangle.setWidth(100D);
            rectangle.setHeight(100D);
            rectangle.setLayoutX(50D);
            rectangle.setLayoutY(100D);
            number.setLayoutX(50D);
            number.setLayoutY(105D);
            title.setLayoutX(200D);
            title.setLayoutY(90D);
            line.setWidth(scene.getWidth() - 225D);
            line.setHeight(2D);
            line.setLayoutX(200D);
            line.setLayoutY(150D);
            artist.setLayoutX(200D);
            artist.setLayoutY(150D);
            extension.setLayoutX(scene.getWidth() - 170D);
            extension.setLayoutY(150D);
            extension.setFitHeight(60D);
            extension.setFitWidth(150D);
            play.setLayoutX(50D);
            play.setLayoutY(scene.getHeight() - 90D);
            duration.setWidth(scene.getWidth() - 225D);
            duration.setHeight(15D);
            duration.setLayoutX(200D);
            duration.setLayoutY(scene.getHeight() - 65D);
            double pos = 0.0D;
            if(mp != null)
                pos = mp.getCurrentTime().toSeconds() / mp.getTotalDuration().toSeconds();
            timeline.setWidth(pos * duration.getWidth());
            timeline.setHeight(15D);
            timeline.setLayoutX(200D);
            timeline.setLayoutY(scene.getHeight() - 65D);
            currentTime.setLayoutX(scene.getWidth() - 250D);
            currentTime.setLayoutY(scene.getHeight() - 135D);
            totalTime.setLayoutX(scene.getWidth() - 150D);
            totalTime.setLayoutY(scene.getHeight() - 135D);
            artwork.setLayoutX(scene.getWidth() / 3D);
            artwork.setLayoutY(scene.getHeight() / 3D);
            artwork.setFitWidth(scene.getWidth() / 3D);
            artwork.setFitHeight(scene.getHeight() / 3D);
            Volume.getInstance().update();
        }
    }

    private void setMetaData(File file)
    {
        File list[] = file.getParentFile().listFiles(new FileFilter() {

            public boolean accept(File file)
            {
                return file.getName().toUpperCase().endsWith("MP3") || file.getName().toUpperCase().endsWith("WAV");
            }
        });
        int nbr = 0;
        int i = 0;
        do
        {
            if(i >= list.length)
                break;
            if(list[i].getName().equals(file.getName()))
            {
                nbr = i + 1;
                break;
            }
            i++;
        } while(true);
        if(file.getName().toUpperCase().endsWith("MP3"))
            extension.setImage(new Image(getClass().getResourceAsStream("/sony/img/mp3.png"), 80D, 80D, false, false));
        else
        if(file.getName().toUpperCase().endsWith("WAV"))
            extension.setImage(new Image(getClass().getResourceAsStream("/sony/img/wav.png"), 80D, 80D, false, false));
        else
            extension.setImage(null);
        if(nbr == 0) {
        	position.setText((new StringBuilder()).append("( ").append(nbr).append(" / ").append(list.length).append(" )").toString());
        	number.setText((new StringBuilder()).append(nbr).append("").toString());
        }
        try
        {
            MP3File mp3 = new MP3File(file);
            Tag tag = mp3.getTag();
            totalTime.setText((new StringBuilder()).append(" / ").append(mp3.getMP3AudioHeader().getTrackLengthAsString()).toString());
            title.setText(tag.getFirst(FieldKey.TITLE));
            artist.setText(tag.getFirst(FieldKey.ARTIST));
            Artwork image = tag.getFirstArtwork();
            if(image != null)
                artwork.setImage(new Image(new ByteArrayInputStream(image.getBinaryData()), 200D, 200D, false, false));
            else
                artwork.setImage(null);
        }
        catch(Exception ex)
        {
            title.setText("");
            artist.setText("");
            artwork.setImage(null);
        }
        if(title.getText().isEmpty())
            title.setText(file.getName());
    }

    private void play()
    {
        try
        {
            Media m = new Media(file.toURI().toASCIIString());
            mp = new MediaPlayer(m);
            mp.setOnEndOfMedia(new Runnable() {

                public void run()
                {
                    right();
                    update();
                }
            });
            mp.play();
        }
        catch(Exception ex) { }
    }

    private void stop()
    {
        if(mp != null)
            mp.stop();
    }

    public void right()
    {
        File list[] = file.getParentFile().listFiles();
        int i = 0;
        do
        {
            if(i >= list.length)
                break;
            if(list[i].getName().equals(file.getName()) && i + 1 < list.length)
            {
                file = list[i + 1];
                stop();
                setMetaData(file);
                play();
                break;
            }
            i++;
        } while(true);
    }

    public void left()
    {
        File list[] = file.getParentFile().listFiles();
        int i = 0;
        do
        {
            if(i >= list.length)
                break;
            if(list[i].getName().equals(file.getName()) && i - 1 >= 0)
            {
                file = list[i - 1];
                stop();
                setMetaData(file);
                play();
                break;
            }
            i++;
        } while(true);
    }

    public void up()
    {
        Volume.getInstance().higher();
    }

    public void down()
    {
        Volume.getInstance().lower();
    }

    public void enter()
    {
        if(mp != null)
        {
            if(javafx.scene.media.MediaPlayer.Status.PLAYING.equals(mp.getStatus()))
            {
                play.setImage(new Image(getClass().getResourceAsStream("/sony/img/pause.png"), 80D, 60D, false, false));
                mp.pause();
            } else
            {
                play.setImage(new Image(getClass().getResourceAsStream("/sony/img/play.png"), 80D, 60D, false, false));
                mp.play();
            }
        } else
        {
            play.setImage(new Image(getClass().getResourceAsStream("/sony/img/play.png"), 80D, 60D, false, false));
            play();
        }
    }

    public void exit()
    {
        mp.stop();
        MemoryStick memory = new MemoryStick(Config.getCategory("Music"));
        memory.setFile(file);
        Main.getScene().setRoot(memory);
        memory.update();
    }

    private File file;
    private MediaPlayer mp;
    private Background bg;
    private HBox status;
    private ImageView logo;
    private Label position;
    private Rectangle rectangle;
    private Label number;
    private Label title;
    private Rectangle line;
    private Label artist;
    private ImageView extension;
    private ImageView play;
    private Rectangle duration;
    private Rectangle timeline;
    private Label currentTime;
    private Label totalTime;
    private ImageView artwork;

}
