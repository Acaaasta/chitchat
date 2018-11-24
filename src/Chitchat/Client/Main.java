package Chitchat.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();

        String file = "src/music/chitchat.mp3";
        Media sound = new Media(new File(file).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public static void main(String[] args) {

        launch(args);
    }

}
