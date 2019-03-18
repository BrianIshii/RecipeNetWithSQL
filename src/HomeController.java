import View.AutoCompletionTextField;
import entity.Recipe;
import entity.User;
import exception.ExecutorException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import service.RecipeService;
import service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeController extends BaseController {
    public static String FXML = "Home.fxml";

    private RecipeService recipeService = RecipeService.getInstance();
    private UserService userService = UserService.getInstance();
    private ArrayList<Long> recipeIDArray = new ArrayList();
    private User user = Main.getUser();

    @FXML private Text name;
    @FXML private Button backButton;
    @FXML private Button forwardButton;
    @FXML ListView<String> listView = new ListView<>();
    @FXML private AutoCompletionTextField searchField = new AutoCompletionTextField();

    public HomeController() {
        super(FXML);
    }

    @FXML
    public void initialize() {

        backButton.setDisable(!canPressBackButton());
        forwardButton.setDisable(!canPressForwardButton());

        user = Main.getUser();
        name.setText("Welcome, " + (user.getFieldValue("name")));

        try {
            List<Recipe> recipes = recipeService.searchByUser(user);

            for(Recipe r : recipes) {
                recipeIDArray.add((Long)r.getFieldValue(Recipe.RID));
                listView.getItems().add((String)r.getField("title").getValue());
            }

            listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    //System.out.println(recipeNameToRecipeID.get(observable.getFieldValue()));
                    RecipeController.recipeID = recipeIDArray.get(listView.getSelectionModel().getSelectedIndex());
                    changeViewTo(RecipeController.FXML);
                }
            });
        } catch (ExecutorException ee) {
            ee.printStackTrace();
            //TODO determine behavior
        }

        // initialize search autocompletion
        List<String> listOfRecipes = new ArrayList<>();
        List<String> listOfUsers = new ArrayList<>();
        try {
            for (Recipe r : recipeService.searchAll()) {
                String title = (String)r.getField(Recipe.TITLE).getValue();
                listOfRecipes.add(title);
            }
            for (User u : userService.searchAll()) {
                String name = (String)u.getField(User.NAME).getValue();
                listOfUsers.add(name);
            }
        } catch (ExecutorException e) {
            e.printStackTrace();
        }

        searchField.getEntries().addAll(listOfRecipes);
        searchField.getEntries().addAll(listOfUsers);

    }

    public void logoutButtonPressed(ActionEvent event) throws IOException {
        logout();
    }

    public void addRecipeButtonPressed(ActionEvent event) throws IOException {
        changeViewTo(AddRecipeController.FXML);
    }

    public void onSearch(ActionEvent event) {

        System.out.println(searchField.getCharacters());
    }


}
