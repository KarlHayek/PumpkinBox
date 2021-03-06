package pumpkinbox.ui.notifications;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Created by ramiawar on 4/4/17.
 */
public class Notification {

    public Notification (String title, String message, int seconds, String code){

        ImageView image;

        switch(code){
            case "SUCCESS":
                image  = new ImageView(new Image("icon_graphics/check_green.png"));
                break;
            case "ERROR":
                image = new ImageView(new Image("icon_graphics/cross_red.png"));
                break;
            case "MESSAGE":
                image = new ImageView(new Image("icon_graphics/message_colored.png"));
                break;
            case "REQUEST":
                image = new ImageView(new Image("icon_graphics/ghost.png"));
                break;
            default:
                image = new ImageView(new Image("icon_graphics/warning_red.png"));
                break;
        }

        Notifications notifications = Notifications.create();
        notifications.title(title);
        notifications.text(message);
        notifications.graphic(image);
        notifications.darkStyle();
        notifications.hideAfter(Duration.seconds(seconds));
        notifications.position(Pos.BOTTOM_RIGHT);
        notifications.show();

    }

    public Notification(String gameTitle, String message){

        ImageView image;

        switch(gameTitle){
        }

    }


}
