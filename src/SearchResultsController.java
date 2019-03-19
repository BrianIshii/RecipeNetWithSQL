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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsController extends BaseController {

  @FXML private Label searchLabel;
  @FXML private Button backButton;
  @FXML private Button forwardButton;
  @FXML private ListView<String> userViewList;
  @FXML private ListView<String> recipeViewList;
  @FXML private AutoCompletionTextField searchField = new AutoCompletionTextField();

  private RecipeService recipeService = RecipeService.getInstance();
  private UserService userService = UserService.getInstance();

  private List<Recipe> recipes = new ArrayList<>();
  private List<User> users = new ArrayList<>();

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

    userViewList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observable, String oldValue, String newValue) {
                User user = users.get(userViewList.getSelectionModel().getSelectedIndex());
                Main.setUser(user);
                changeViewTo(HomeController.FXML);
                System.out.println(user.getField(User.NAME).getValue());
              }
            });
    recipeViewList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observable, String oldValue, String newValue) {
                RecipeController.recipeID =
                    (Long)
                        recipes
                            .get(recipeViewList.getSelectionModel().getSelectedIndex())
                            .getFieldValue(Recipe.RID);
                changeViewTo(RecipeController.FXML);
              }
            });
  }

  private void init_autocomplete() {

    List<String> listOfRecipes = new ArrayList<>();
    List<String> listOfUsers = new ArrayList<>();
    try {
      for (Recipe r : recipeService.searchAll()) {
        String title = (String) r.getFieldValue(Recipe.TITLE);
        listOfRecipes.add(title);
      }
      for (User u : userService.searchAll()) {
        String name = (String) u.getFieldValue(User.NAME);
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

    recipeViewList.getItems().clear();
    userViewList.getItems().clear();
    users.clear();
    recipes.clear();

    searchEntities();
  }

  private void searchEntities() {
    try {
      users = userService.fuzzyNameSearch(searchTerm, EDIT_DIST);
      recipes = recipeService.fuzzyNameSearch(searchTerm, EDIT_DIST);

      for (Recipe r : recipes) {
        recipeViewList.getItems().add((String) r.getFieldValue(Recipe.TITLE));
      }
      for (User u : users) {
        userViewList.getItems().add((String) u.getFieldValue(User.NAME));
      }
      System.out.println(recipes);
      System.out.println(users);
    } catch (ExecutorException e) {
      e.printStackTrace();
    }
  }

    public void homeButtonPressed(ActionEvent event) throws IOException {
        changeViewTo(HomeController.FXML);
    }
}
