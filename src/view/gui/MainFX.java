package view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/programChooser.fxml"));

        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Select Program");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
