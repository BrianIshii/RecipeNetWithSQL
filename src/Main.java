import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main  extends Application {

   public static void main(String[] args) {
       launch(args);
   }
   DatabaseConnectionSingleton connection = DatabaseConnectionSingleton.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (connection.getConnection() != null) {
            System.out.println("working");
        }
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("OurSQL");
        primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.show();
    }
}