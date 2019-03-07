import Entity.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static Stage primaryStage = null;
    private static User user = null;

    public static Stage getPrimaryStage() {
    return primaryStage;
  }

    public static User getUser() {
    return user;
  }

    public static void setUser(User user) {
    Main.user = user;
  }

    public static void main(String[] args) {
    launch(args);
  }


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("MainController.fxml"));
        primaryStage.setTitle("OurSQL");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
}
