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
import java.util.Arrays;
import java.util.List;

public class AddRecipeController extends BaseController {

    public static String FXML = "AddRecipe.fxml";

    @FXML TextField recipeNameTextField;
    @FXML TextField ingredientTextField;
    @FXML TextField amountTextField;
    @FXML TextField unitTextField;
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

        if (isValidIngredientInput()) {
            String s = String.format("%s, %s, %s", ingredientTextField.getText(), amountTextField.getText(), unitTextField.getText());
            ingredientsView.getItems().add(s);

            clearIngredientTextFields();
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

            List<String> fields = Arrays.asList(s.split(","));
            recipe.addIngredient(new Ingredient(fields.get(0)), Integer.parseInt(fields.get(1)), fields.get(2));
        }
    }

    private void addAllInstructions(Recipe recipe) {
        for (String s: instructionsView.getItems()) {
            recipe.addInstruction(s);
        }
    }

    private boolean isValidInput(TextField textFiled) {

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

    private boolean isValidIngredientInput() {

        if (isValidInput(ingredientTextField) &&
                isValidInput(amountTextField) &&
                isValidInput(unitTextField)) {
            return true;
        }

        return false;
    }

    private void clearIngredientTextFields() {
        ingredientTextField.clear();
        amountTextField.clear();
        unitTextField.clear();
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
