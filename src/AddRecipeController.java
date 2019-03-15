import entity.Ingredient;
import entity.Instruction;
import entity.Recipe;
import entity.User;
import exception.DuplicateEntryException;
import exception.ExecutorException;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import service.RecipeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.Date;

public class AddRecipeController extends BaseController {

    public static String FXML = "AddRecipe.fxml";

    @FXML TextField recipeNameTextField;
    @FXML TextField ingredientTextField;
    @FXML TextField instructionTextField;
    @FXML ListView<String> ingredientsView = new ListView<>();
    @FXML ListView<String> instructionsView = new ListView<>();

    public AddRecipeController() {
        super(FXML);
    }

    public void saveButtonPressed(ActionEvent event) throws IOException {
        resetTextFieldsStyle();

        if (isValidInput(recipeNameTextField)) {
            // New recipe
            Recipe recipe = new Recipe(recipeNameTextField.getText(), "url", Main.getUser(), new Date(20180101), 4);

            // Add data
            addAllIngredients(recipe);
            addAllInstructions(recipe);

            // Commit
            try {
                recipeService.save(recipe);

                // Transit view
                changeViewTo(HomeController.FXML);
            } catch(DuplicateEntryException dee) {
                dee.printStackTrace();
                //TODO add failure behavior
            } catch(ExecutorException ee) {
                ee.printStackTrace();
                //TODO add failure behavior
            }
        }
    }

    @FXML
    public void addIngredientToList() {
        resetTextFieldsStyle();

        if (isValidInput(ingredientTextField)) {
            // Add to listView
            ingredientsView.getItems().add(ingredientTextField.getText());

            // Clear textField
            ingredientTextField.clear();
        }
    }

    @FXML
    public void addInstructionToList() {
        resetTextFieldsStyle();

        if (isValidInput(instructionTextField)) {
            // Add to listView
            instructionsView.getItems().add(instructionTextField.getText());

            // Clear textField
            instructionTextField.clear();
        }
    }

    private void addAllIngredients(Recipe recipe) {
        for (String s: ingredientsView.getItems()) {
            // TODO:: Change amount and unit
            recipe.addIngredient(new Ingredient(s), 1, "cup");
        }
    }

    private void addAllInstructions(Recipe recipe) {
        for (String s: instructionsView.getItems()) {
            recipe.addInstruction(s);
        }
    }

    private boolean isValidInput(TextField textFiled) {
        // Check empty fields
        if (textFiled.getText().isEmpty()) {
            alertTextField(textFiled);

            // Alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText(null);
            alert.setContentText("Field cannot be empty");
            alert.showAndWait();

            return false;
        }

        return true;
    }

    private void alertTextField(TextField textField) {
        textField.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
    }

    private void resetTextFieldsStyle() {
        recipeNameTextField.setStyle("");
        ingredientTextField.setStyle("");
        instructionTextField.setStyle("");
    }
}
