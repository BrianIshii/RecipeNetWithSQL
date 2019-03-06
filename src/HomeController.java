import Entity.Recipe;
import Entity.User;
import Service.RecipeService;
import Service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeController {
    private UserService userService = UserService.getInstance();
    private RecipeService recipeService = RecipeService.getInstance();
    private User user = Main.getUser();
    ObservableList<String> items = FXCollections.<String>observableArrayList("hi");
    @FXML ListView<String> listView = new ListView<>(items);
    @FXML private Text name;

    @FXML
    public void initialize() {
        user = Main.getUser();
        System.out.println(user);

        name.setText("Welcome, " + ((String) user.getValue("name")));

        List<Recipe> recipes = recipeService.searchByUser(user);

        List<String> titles = new ArrayList<>();

        for(Recipe r : recipes) {
            listView.getItems().add((String)r.getField("title").getValue());
        }

        items = FXCollections.observableArrayList(titles);

    }

    public void pressButton(ActionEvent event) {
    }
}
