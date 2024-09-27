package sony;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import sony.xmb.Background;
import sony.xmb.Battery;
import sony.xmb.Clock;
import sony.xmb.Volume;

public class Radio extends Pane
    implements Base
{
    private class Logo extends ImageView
    {

        public void update()
        {
            if(index + position < list.length)
                setImage(new Image(logo[index + position], 200D, 150D, false, false));
            else
                setImage(null);
            if(getScene() != null)
            {
                double box = getScene().getWidth() / 14D;
                setFitHeight(box);
                setFitWidth(box);
                setLayoutX(4D * box + (double)(index * 2) * box);
                setLayoutY(getHeight() - 2D * box);
            }
        }

        private int index;

        public Logo(int index)
        {
            super();
            this.index = index;
            if(index == 0)
            {
                final DropShadow dropShadow = new DropShadow();
                dropShadow.setOffsetX(0.0D);
                dropShadow.setOffsetY(0.0D);
                dropShadow.setColor(Color.WHITE);
                KeyFrame kf = new KeyFrame(Duration.millis(80D), new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent event)
                    {
                        if(direction)
                        {
                            dropShadow.setRadius(dropShadow.getRadius() - 10D);
                            dropShadow.setSpread(dropShadow.getSpread() - 0.10000000000000001D);
                        } else
                        {
                            dropShadow.setRadius(dropShadow.getRadius() + 10D);
                            dropShadow.setSpread(dropShadow.getSpread() + 0.10000000000000001D);
                        }
                        if(dropShadow.getSpread() > 0.5D)
                            direction = true;
                        if(dropShadow.getSpread() < 0.0D)
                            direction = false;
                    }

                    private boolean direction=true;
                });
                Timeline repeat = new Timeline(kf);
                repeat.setCycleCount(Timeline.INDEFINITE);
                repeat.play();
                setEffect(dropShadow);
            }
        }
    }

    public Radio()
    {
        bg = new Background();
        status = new HBox(8D);
        clock = new Clock();
        battery = new Battery();
        play = false;
        image = new ImageView();
        position = 0;
        current = 0;
        dir = new File((new StringBuilder()).append(Main.getUserHome()).append("/Sony/Radio").toString());
        if(!dir.exists()) {
        	dir.mkdirs();
        }
        filter = new FileFilter() {

            public boolean accept(File file)
            {
                return file.getName().toUpperCase().endsWith("XML");
            }
        };
        list = dir.listFiles(filter);
        logo = new String[list.length];
        stream = new String[logo.length];
        load();
        getChildren().add(bg);
        status.getChildren().add(clock);
        status.getChildren().add(battery);
        getChildren().add(status);
        getChildren().add(image);
        getChildren().add(new Logo(0));
        getChildren().add(new Logo(1));
        getChildren().add(new Logo(2));
        getChildren().add(new Logo(3));
        getChildren().add(new Logo(4));
        getChildren().add(Volume.getInstance());
        play();
    }

    private void load()
    {
    	int i=0;
        for(File file:list) {
        	final int index = i;
	        try
	        {
	            SAXParserFactory factory = SAXParserFactory.newInstance();
	            SAXParser saxParser = factory.newSAXParser();
	            saxParser.parse(file, new DefaultHandler() {
	
	                public void startElement(String uri, String localName, String qName, Attributes attributes)
	                    throws SAXException
	                {
	                    logoFlag = qName.equalsIgnoreCase("logo");
	                    streamFlag = qName.equalsIgnoreCase("stream");
	                }
	
	                public void endElement(String uri, String localName, String qName)
	                    throws SAXException
	                {
	                    logoFlag = false;
	                    streamFlag = false;
	                }
	
	                public void characters(char ch[], int start, int length)
	                    throws SAXException
	                {
	                    String charsToAppend = new String(ch, start, length);
	                    if(logoFlag)
	                        logo[index] = charsToAppend;
	                    if(streamFlag)
	                        stream[index] = charsToAppend;
	                }
	
	                private boolean logoFlag=false;
	                private boolean streamFlag=false;
	            });
	        }
	        catch(Exception ex) { }
	        i++;
        }
    }

    private void play()
    {
        try
        {
            File radio = File.createTempFile("radio", ".mp3");
            radio.deleteOnExit();
            final FileOutputStream buffer = new FileOutputStream(radio);
            URL u = new URL(stream[current]);
            final InputStream din = u.openStream();
            if(t != null) {
            	play = false;
                while(t.isAlive()) {
                	// do nothing
                }
            }
            play = true;
            t = new Thread() {

                public void run()
                {
                    try
                    {
                        for(int c = -1; play && (c = din.read()) != -1;)
                            buffer.write(c);

                        din.close();
                        buffer.close();
                    }
                    catch(IOException ex) { }
                }
            };
            play = true;
            t.start();
            Thread.sleep(1000L);
            Media m = new Media(radio.toURI().toASCIIString());
            mp = new MediaPlayer(m);
            mp.setOnEndOfMedia(new Runnable() {

                public void run()
                {
                    mp.stop();
                    play();
                }
            });
            mp.setOnStopped(new Runnable() {

                public void run()
                {
                    if(play)
                        mp.play();
                }

            });
            mp.play();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void left()
    {
        if(position > 0)
            position--;
    }

    public void right()
    {
        if(position + 1 < dir.list().length)
            position++;
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
        if(position == current && mp != null)
        {
            if(javafx.scene.media.MediaPlayer.Status.PLAYING.equals(mp.getStatus()))
                mp.pause();
            else
                mp.play();
        } else
        {
            current = position;
            play = false;
            if(mp != null)
                mp.stop();
            play();
        }
    }

    public void update()
    {
        if(!getChildren().contains(Volume.getInstance()))
            getChildren().add(Volume.getInstance());
        Scene scene = getScene();
        if(scene != null)
        {
            bg.setFitWidth(scene.getWidth());
            bg.setFitHeight(scene.getHeight());
            status.setLayoutX(0.0D);
            status.setLayoutY(0.0D);
            status.setPrefHeight(80D);
            status.setAlignment(Pos.TOP_RIGHT);
            status.setPrefWidth(scene.getWidth());
            clock.setFont(new Font(scene.getHeight() / 20D));
            battery.setFitWidth(scene.getWidth() / 10D);
            battery.setFitHeight(scene.getHeight() / 10D);
            image.setLayoutX(scene.getWidth() / 3D);
            image.setLayoutY(scene.getHeight() / 3D);
            image.setFitWidth(scene.getWidth() / 3D);
            image.setFitHeight(scene.getHeight() / 3D);
            image.setImage(new Image(this.logo[current], 200D, 150D, false, false));
            Iterator<Node> i$ = getChildren().iterator();
            do
            {
                if(!i$.hasNext())
                    break;
                Node logo = (Node)i$.next();
                if(logo instanceof Logo)
                    ((Logo)logo).update();
            } while(true);
            Volume.getInstance().update();
        }
        if(mp != null)
            mp.setVolume(Volume.getInstance().getVolume());
    }

    public void exit()
    {
        play = false;
        if(mp != null) {
            mp.stop();
            mp.dispose();
        }
        Main.getScene().setRoot(Main.getMenu());
        Main.getMenu().update();
    }

    private Background bg;
    private HBox status;
    private Clock clock;
    private Battery battery;
    private boolean play;
    private MediaPlayer mp;
    private ImageView image;
    private int position;
    private int current;
    private File dir;
    private FileFilter filter;
    private File list[];
    private String logo[];
    private String stream[];
    private Thread t;

}
