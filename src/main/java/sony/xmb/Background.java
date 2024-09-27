package sony.xmb;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sony.Main;

public class Background extends ImageView {

    public Background() {
        if(Main.getMenu() != null)
            setImage(Main.getMenu().getTheme());
        else
            setImage(new Image(getClass().getResourceAsStream("/sony/img/wave.gif")));
    }
}
