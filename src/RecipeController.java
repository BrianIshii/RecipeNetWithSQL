import Entity.Recipe;
import Service.RecipeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class RecipeController extends BaseController {
    public static String FXML = "Recipe.fxml";
    public static Long recipeID = Long.valueOf(0);

    private RecipeService recipeService = RecipeService.getInstance();

    @FXML private Button backButton;
    @FXML private Button forwardButton;

    public RecipeController() {
        super(FXML);
    }

    @javafx.fxml.FXML
    public void initialize() {

        backButton.setDisable(!canPressBackButton());
        forwardButton.setDisable(!canPressForwardButton());

        Recipe r = recipeService.searchById(recipeID);
//        System.out.println(r);
    }
    public void homeButtonPressed(ActionEvent event) throws IOException {
        changeViewTo(HomeController.FXML);
    }
}
