import entity.Ingredient;
import entity.Recipe;
import entity.User;
import service.RecipeService;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Date;

public class AddRecipeController extends BaseController {
    public static String FXML = "AddRecipe.fxml";

    public AddRecipeController() {
        super(FXML);
    }


    public void saveButtonPressed(ActionEvent event) throws IOException {
        Recipe newRecipe = new Recipe("name", "url", Main.getUser(), new Date(20180101), 4);
        //newRecipe.addIngredient(new Ingredient("avacado"), 1, "cup");
        newRecipe.addInstruction("do this first");
        recipeService.save(newRecipe);
        changeViewTo(HomeController.FXML);
    }
}
