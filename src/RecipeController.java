import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class RecipeController extends BaseController {
   public static Long recipeID = Long.valueOf(0);
    public static String FXML = "Recipe.fxml";
    @FXML private Button backButton;
    @FXML private Button forwardButton;

    public RecipeController() {
        super(FXML);
    }

    @javafx.fxml.FXML
    public void initialize() {

        backButton.setDisable(!canPressBackButton());
        forwardButton.setDisable(!canPressForwardButton());


    }
    public void homeButtonPressed(ActionEvent event) throws IOException {
        changeViewTo(HomeController.FXML);
    }
}
