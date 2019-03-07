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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeController extends BaseController {
    public static String FXML = "Home.fxml";
    private RecipeService recipeService = RecipeService.getInstance();
    private User user = Main.getUser();
    @FXML ListView<String> listView = new ListView<>();
    @FXML private Text name;
    @FXML private Button backButton;
    @FXML private Button forwardButton;

    public HomeController() {
        super(FXML);
    }

    @FXML
    public void initialize() {

        backButton.setDisable(!canPressBackButton());
        forwardButton.setDisable(!canPressForwardButton());

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
                System.out.println(oldValue);
                changeViewTo(RecipeController.FXML);
            }
        });
    }

    public void logoutButtonPressed(ActionEvent event) throws IOException {
        logout();
    }



}
