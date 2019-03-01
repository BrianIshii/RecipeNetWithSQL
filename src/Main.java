import Service.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  DatabaseConnection connection = DatabaseConnection.getInstance();

  @Override
  public void start(Stage primaryStage) throws Exception {
    if (connection.getConnection() != null) {
      System.out.println("working");
    }
    Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
    primaryStage.setTitle("OurSQL");
    primaryStage.setScene(new Scene(root, 600, 400));
    primaryStage.show();
  }
}
