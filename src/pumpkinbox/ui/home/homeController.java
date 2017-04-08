package pumpkinbox.ui.home;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import pumpkinbox.api.NotificationObject;
import pumpkinbox.api.User;
import pumpkinbox.client.ChatClient;
import pumpkinbox.client.Client;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.icons.Icons;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.ui.images.Images;
import pumpkinbox.ui.notifications.Notification;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ramiawar on 3/23/17.
 */
public class homeController implements Initializable{

    private String authenticationToken;
    private String online_status;
    private int userId;
    private String name;

    private ObservableList<String> friendsList = FXCollections.observableArrayList();

    private BlockingQueue<String> messages_queue = new LinkedBlockingQueue<>();
    private BlockingQueue<NotificationObject> notification_queue = new LinkedBlockingQueue<>();

    private final BlockingQueue<User> onlineFriends = new LinkedBlockingQueue<>();
    private ArrayList<String> onlineFriendsNames = new ArrayList<>();
    public void setAuthenticationToken(String s){
        this.authenticationToken = s;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setUserId(int id){
        this.userId = id;
    }
    public void initClient(){
        System.out.println("Initializing client with : " + authenticationToken + name);

        client = new ChatClient(onlineFriends, messages_queue, notification_queue, userId, authenticationToken);
        client.connect();
    }


    Icons icons = new Icons();

    ChatClient client;

    @FXML
    Label closeIcon;
    @FXML
    HBox menuBar;
    @FXML
    Label minimizeIcon;
    @FXML
    Region draggableRegion;
    @FXML
    Label searchIcon;
    @FXML
    Label user_status;
    @FXML
    ContextMenu statusMenu;
    @FXML
    ListView friends_list;
    @FXML
    Label username;
    @FXML
    ImageView profile_photo;
    @FXML
    JFXButton add_friend;
    @FXML
    Label status_icon;



    private Stage stage;

    //Receiving stage from main class to make window draggable
    public void registerStage(Stage stage){

        this.stage = stage;
        EffectUtilities.makeDraggable(this.stage, this.menuBar);
        EffectUtilities.makeDraggable(this.stage, this.draggableRegion);

    }

    void close() {
        stage.close();
    }

    @FXML
    void close(ActionEvent e) {
        stage.close();
    }

    @FXML
    void setStatusOnline(ActionEvent e){
        user_status.setText("Online");
        status_icon.setGraphic(icons.GHOST_GREEN);
        online_status = "online";
    }
    @FXML
    void setStatusOffline(ActionEvent e){
        user_status.setText("Offline");
        status_icon.setGraphic(icons.GHOST_RED);
        online_status = "offline";
    }
    @FXML
    void setStatusAway(ActionEvent e){
        user_status.setText("Away");
        status_icon.setGraphic(icons.GHOST_ORANGE);
        online_status = "away";
    }

    @FXML
    void loadAddFriend(ActionEvent e){

    }

    void loadWindow(String location, String title){
        try {

            Parent parent = FXMLLoader.load(getClass().getResource(location));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.setAlwaysOnTop(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadIcons();

        closeIcon.setOnMouseClicked((MouseEvent e) ->{
            close();
        });
        minimizeIcon.setOnMouseClicked((MouseEvent e) ->{
            minimize();
        });
        user_status.setOnMouseClicked(event -> {
            statusMenu.show(user_status, event.getScreenX(), event.getScreenY());
        });

        username.setText(name);

        profile_photo.setImage(Images.pumpkin_logo);

        Timeline gui_updater = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                try {
                    updateGUI();

                } catch (InterruptedException e) {
                    System.out.println("Error taking from queue.");
                    e.printStackTrace();
                }


//                System.out.println(authenticationToken);
//                System.out.println(userId);
            }
        }));
        gui_updater.setCycleCount(Timeline.INDEFINITE);
        gui_updater.play();

    }

    private void minimize(){
        stage.setIconified(true);
    }

    public void loadIcons(){

        //Setting Icons on buttons using **ui.Icons.Icons** class
        icons.setSize("1em");

        closeIcon.setGraphic(icons.CLOSE_m);
        searchIcon.setGraphic(icons.SEARCH);
        user_status.setGraphic(icons.MINIMIZE_small);
        minimizeIcon.setGraphic(icons.MINIMIZE_m);
        add_friend.setGraphic(icons.ADD);
        status_icon.setGraphic(icons.GHOST_GREEN);

    }

    public void updateGUI() throws InterruptedException {

        System.out.println("Updating friends list...");

        if(!name.equals(null)) username.setText(name);

        checkAndAdd(onlineFriends, onlineFriendsNames);

        System.out.println("MAIN: " + onlineFriendsNames);


        friendsList.setAll(onlineFriendsNames);
        friends_list.getItems().setAll(onlineFriendsNames);

        if(!notification_queue.isEmpty()){
            NotificationObject notificationObject = new NotificationObject();
            try {
                notificationObject = notification_queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Notification notif = new Notification(notificationObject.getSenderUsername(), notificationObject.getSenderUsername(), 5, "MESSAGE");
        }

    }

    private void checkAndAdd(BlockingQueue<User> friends, ArrayList<String> array) throws InterruptedException {

        while(!friends.isEmpty()){

            User friend = friends.take();

            if(!array.contains(friend.getUsername())){
                array.add(friend.getUsername());
            }
        }
    }
}
