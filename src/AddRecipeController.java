import View.AutoCompletionTextField;
import entity.Ingredient;
import entity.Recipe;
import exception.DuplicateEntryException;
import exception.ExecutorException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import service.IngredientService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.Date;
import java.util.*;

public class AddRecipeController extends BaseController {

    public static String FXML = "AddRecipe.fxml";
    private static IngredientService ingredientService = IngredientService.getInstance();
    @FXML AutoCompletionTextField ingredientTextField = new AutoCompletionTextField();
    @FXML TextField amountTextField;
    @FXML TextField unitTextField;
    @FXML TextField instructionTextField;
    @FXML ListView<String> ingredientsView = new ListView<>();
    @FXML ListView<String> instructionsView = new ListView<>();
    @FXML Label recipeNameLabel;
    @FXML Button recipeNameButton;
    @FXML private Button backButton;
    @FXML private Button forwardButton;
    private String selectedIngredient;
    private String selectedInstruction;
    private Map<String, Ingredient> ingredients = new HashMap<>();
    private List<String> listOfIngredients = new ArrayList<>();

    public AddRecipeController() {
        super(FXML);
    }
    @FXML
    public void initialize() {

        try {
            for (Ingredient i : ingredientService.searchAll()) {
                String name = (String)i.getField(Ingredient.NAME).getValue();
                ingredients.put(name, i);
                listOfIngredients.add(name);
            }
        } catch (ExecutorException e) {
            e.printStackTrace();
        }

        ingredientTextField.getEntries().addAll(listOfIngredients);

        backButton.setDisable(!canPressBackButton());
        forwardButton.setDisable(!canPressForwardButton());

        ingredientsView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedIngredient = observable.getValue();
            }
        });

        instructionsView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedInstruction = observable.getValue();
            }
        });
    }

    public void deleteIngredientButtonPressed(ActionEvent event) throws IOException {
        if (selectedIngredient == null) {
            return;
        }

        ingredientsView.getItems().remove(selectedIngredient);

        ingredientsView.getSelectionModel().clearSelection();
        selectedIngredient = null;
    }

    public void deleteInstructionButtonPressed(ActionEvent event) throws IOException {
        if (selectedInstruction == null) {
            return;
        }

        instructionsView.getItems().remove(selectedInstruction);

        instructionsView.getSelectionModel().clearSelection();
        selectedInstruction = null;
    }


    public void saveButtonPressed(ActionEvent event) throws IOException {
        resetTextFieldsStyle();

        // New recipe
        Recipe recipe = new Recipe(recipeNameLabel.getText(), "url", Main.getUser(), new Date(20180101), 4);

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

    @FXML
    public void changeRecipeName() {
        TextInputDialog dialog = new TextInputDialog(recipeNameLabel.getText());
        dialog.setTitle("Rename Recipe");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Recipe Name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if (!result.get().replaceAll("\\s+","").isEmpty()) {
                recipeNameLabel.setText(result.get());
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

            List<String> fields = Arrays.asList(s.split(", "));
            Ingredient i = ingredients.get(fields.get(0));
            if (i == null) {
                i = new Ingredient(fields.get(0));
            }
            recipe.addIngredient(i, Integer.parseInt(fields.get(1)), fields.get(2));
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
        ingredientTextField.setStyle("");
        instructionTextField.setStyle("");
    }
}
