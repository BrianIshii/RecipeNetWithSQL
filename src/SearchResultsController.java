import View.AutoCompletionTextField;
import entity.Entity;
import entity.Recipe;
import entity.User;
import exception.ExecutorException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import service.RecipeService;
import service.UserService;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsController extends BaseController {

    @FXML private Label searchLabel;
    @FXML private Button backButton;
    @FXML private Button forwardButton;
    @FXML private ListView<String> resultsList;
    @FXML private AutoCompletionTextField searchField = new AutoCompletionTextField();

    private RecipeService recipeService = RecipeService.getInstance();
    private UserService userService = UserService.getInstance();

    private List<User> users;
    private List<Long> recipeIDList = new ArrayList<>();

    boolean isRecipe = true;

    private static final int EDIT_DIST = 3;

    private static String searchTerm;
    public static String FXML = "SearchResults.fxml";

    public SearchResultsController() {
        super(FXML);
    }

    public static void setSearchTerm(String st) {
        searchTerm = st;
    }

    @FXML
    public void initialize() {

        backButton.setDisable(!canPressBackButton());
        forwardButton.setDisable(!canPressForwardButton());
        searchLabel.setText(searchTerm);

        init_autocomplete();

        searchEntities();


        resultsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if(!recipeIDList.isEmpty()) {
                    RecipeController.recipeID = recipeIDList.get(resultsList.getSelectionModel().getSelectedIndex());
                    changeViewTo(RecipeController.FXML);
                }
                else if(!users.isEmpty()) {
                    //TODO throwing assertion error
                    User user = users.get(resultsList.getSelectionModel().getSelectedIndex());
                    Main.setUser(user);
                    changeViewTo(HomeController.FXML);
                    System.out.println(user.getField(User.NAME).getValue());
                }
            }
        });
    }

    private void init_autocomplete() {

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



    public void onSearch(ActionEvent event) {

        SearchResultsController.setSearchTerm(searchField.getCharacters().toString());
        searchLabel.setText(searchTerm);

        resultsList.getItems().clear();
        recipeIDList = new ArrayList<>();
        users.clear();

        searchEntities();
    }


    private void searchEntities() {
        try {
            users = userService.fuzzyNameSearch(searchTerm, EDIT_DIST);
            List<Recipe> recipes = recipeService.fuzzyNameSearch(searchTerm, EDIT_DIST);

            if(!recipes.isEmpty()) {
                for(Recipe r : recipes) {
                    resultsList.getItems().add((String) r.getField(Recipe.TITLE).getValue());
                    recipeIDList.add((Long)r.getFieldValue(Recipe.RID));
                }
            }
            else {
                isRecipe = false;
                for(User u : users) {
                    resultsList.getItems().add((String) u.getField(User.NAME).getValue());
                }
            }
        } catch (ExecutorException e) {
            e.printStackTrace();
        }
    }
}
