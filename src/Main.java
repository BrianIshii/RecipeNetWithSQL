import Entity.User;
import Service.DatabaseConnection;
import Service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  DatabaseConnection connection = DatabaseConnection.getInstance();
  private static Stage primaryStage = null;

  public static Stage getPrimaryStage() {
    return primaryStage;
  }

  public static void main(String[] args) {
    launch(args);
  }


  @Override
  public void start(Stage primaryStage) throws Exception {
    UserService userService = UserService.getInstance();

    if (connection.getConnection() != null) {
      System.out.println("working");
    }
    userService.save(new User("brian", "brianlishii@gmail.com", "password"));

    this.primaryStage = primaryStage;
    Parent root = FXMLLoader.load(getClass().getResource("MainController.fxml"));
    primaryStage.setTitle("OurSQL");
    primaryStage.setScene(new Scene(root, 600, 400));
    primaryStage.show();
  }
}
