import Entity.Recipe;
import Entity.User;
import Service.RecipeService;
import Service.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeController {
    private UserService userService = UserService.getInstance();
    private RecipeService recipeService = RecipeService.getInstance();
    private User user = Main.getUser();
    @FXML ListView<String> listView = new ListView<>();
    @FXML private Text name;

    @FXML
    public void initialize() {
        user = Main.getUser();
        System.out.println(user);

        name.setText("Welcome, " + ((String) user.getValue("name")));

        List<Recipe> recipes = recipeService.searchByUser(user);

        List<String> titles = new ArrayList<>();

        ObservableList<String> recipeNames = FXCollections.observableArrayList();

        for(Recipe r : recipes) {
            listView.getItems().add((String)r.getField("title").getValue());
        }

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource(RecipeController.FXML));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Main.getPrimaryStage().getScene().setRoot(root);
            }
        });
    }

    public void logoutButtonPressed(ActionEvent event) throws IOException {
        Main.setUser(null);
        Parent root = FXMLLoader.load(getClass().getResource(MainController.FXML));
        Main.getPrimaryStage().getScene().setRoot(root);
    }
}
