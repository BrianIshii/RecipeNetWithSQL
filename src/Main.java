import Entity.User;
import Service.DatabaseConnection;
import Service.RecipeService;
import Service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  DatabaseConnection connection = DatabaseConnection.getInstance();
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
    if (connection.getConnection() != null) {
      System.out.println("working");
    }

    this.primaryStage = primaryStage;
    Parent root = FXMLLoader.load(getClass().getResource("MainController.fxml"));
    primaryStage.setTitle("OurSQL");
    primaryStage.setScene(new Scene(root, 600, 400));
    primaryStage.show();
  }
}
