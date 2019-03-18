import View.AutoCompletionTextField;
import View.Unit;
import entity.Ingredient;
import entity.IngredientRecipe;
import entity.Instruction;
import entity.Recipe;
import exception.DuplicateEntryException;
import exception.ExecutorException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.IngredientService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.Date;
import java.util.*;

public class AddRecipeController extends BaseController {

    public static Recipe currentRecipe;
    public static String FXML = "AddRecipe.fxml";
    private static IngredientService ingredientService = IngredientService.getInstance();

    @FXML ListView<String> ingredientsView = new ListView<>();
    @FXML ListView<String> instructionsView = new ListView<>();
    @FXML Label recipeNameLabel;
    @FXML Button editRecipeImageButton;
    @FXML ImageView recipeImageView;
    @FXML private Button backButton;
    @FXML private Button forwardButton;
    @FXML Button removeIngredientButton;
    @FXML Button removeInstructionButton;
    private String selectedIngredient;
    private String selectedInstruction;
    private Map<String, Ingredient> ingredients = new HashMap<>();
    private Map<Long, String> ingredientNameByID = new HashMap<>();
    private List<String> listOfIngredients = new ArrayList<>();
    private String imageURL = "";

    public AddRecipeController() {
        super(FXML);
    }

    @FXML
    public void initialize() {

        editRecipeImageButton.setVisible(false);

        try {
            for (Ingredient i : ingredientService.searchAll()) {
                String name = (String)i.getFieldValue(Ingredient.NAME);
                ingredients.put(name, i);
                ingredientNameByID.put((Long)(i.getFieldValue(Ingredient.IID)), name);
                listOfIngredients.add(name);
            }
        } catch (ExecutorException e) {
            e.printStackTrace();
        }

        backButton.setDisable(!canPressBackButton());
        forwardButton.setDisable(!canPressForwardButton());

        removeIngredientButton.setOpacity(0.3);
        removeInstructionButton.setOpacity(0.3);
        removeIngredientButton.setDisable(true);
        removeInstructionButton.setDisable(true);

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

        if (currentRecipe != null) {
            recipeNameLabel.setText((String) currentRecipe.getFieldValue(Recipe.TITLE));
            String url = (String) currentRecipe.getFieldValue(Recipe.URL);
            if (!url.isEmpty()) {
                recipeImageView.setImage(new Image(url));
            }

            for(IngredientRecipe i : currentRecipe.getIngredients()) {
                addIngredientToList(ingredientNameByID.get((Long)i.getFieldValue(IngredientRecipe.IID)),
                        i.getFieldValue(IngredientRecipe.AMOUNT).toString(),
                        (String)i.getFieldValue(IngredientRecipe.UNIT));
            }

            for(Instruction i : currentRecipe.getInstructions()) {
                addInstructionToList((String)i.getFieldValue(Instruction.DESCRIPTION));
            }
        }
    }

    @FXML
    public void removeIngredientButtonPressed(ActionEvent event) throws IOException {
        if (selectedIngredient == null) {
            // Alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText(null);
            alert.setContentText("Select an ingredient to remove");
            alert.showAndWait();

            return;
        }

        ingredientsView.getItems().remove(selectedIngredient);

        ingredientsView.getSelectionModel().clearSelection();
        selectedIngredient = null;

        if (ingredientsView.getItems().size() == 0) {
            removeIngredientButton.setOpacity(0.3);
            removeIngredientButton.setDisable(true);
        }
    }


    public void saveButtonPressed(ActionEvent event) throws IOException {
        //String defaultImageURL = "resources/img/recipe_default.jpg";

        Recipe recipe;
        if (currentRecipe == null) {
            // New recipe
            recipe = new Recipe(recipeNameLabel.getText(),
                    imageURL,
                    Main.getUser(), new Date(20180101), 4);

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
                // New recipe
            }
            currentRecipe = null;
        } else {

            currentRecipe.setFieldValue(Recipe.TITLE,recipeNameLabel.getText());
            currentRecipe.setFieldValue(Recipe.URL, imageURL);
            for(IngredientRecipe i : currentRecipe.getIngredients()) {
                currentRecipe.removeIngredient((Long)i.getFieldValue(IngredientRecipe.IID));
            }
            addAllIngredients(currentRecipe);

            currentRecipe.removeAllInstructions();

            if (instructionsView.getItems().size() > 0) {
                addAllInstructions(currentRecipe);
            }

            recipe = currentRecipe;

            // Commit
            try {
                recipeService.save(recipe);

                // Transit view
                RecipeController.recipeID = (Long) currentRecipe.getFieldValue(Recipe.RID);
                changeViewTo(RecipeController.FXML);
            } catch(DuplicateEntryException dee) {
                dee.printStackTrace();
                //TODO add failure behavior
            } catch(ExecutorException ee) {
                ee.printStackTrace();
                //TODO add failure behavior
                // New recipe
            }
            currentRecipe = null;
        }
    }

    @FXML
    public void changeRecipeNameButtonPressed() {
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
    public void addIngredientButtonPressed() {
        Dialog<AddIngredientResult> dialog = new Dialog<>();
        dialog.setTitle("Add Ingredient");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Name
        Label nameLabel = new Label("Ingredient Name:");
        nameLabel.setPrefWidth(150);
        nameLabel.setAlignment(Pos.CENTER_RIGHT);
        nameLabel.setPadding(new Insets(0, 5, 0, 0));
        AutoCompletionTextField nameTextField = new AutoCompletionTextField();
        nameTextField.getEntries().addAll(listOfIngredients);
        HBox hbox1 = new HBox(nameLabel, nameTextField);
        hbox1.setAlignment(Pos.CENTER);

        // Amount
        Label amountLabel = new Label("Amount:");
        amountLabel.setPrefWidth(150);
        amountLabel.setAlignment(Pos.CENTER_RIGHT);
        amountLabel.setPadding(new Insets(0, 5, 0, 0));
        TextField amountTextField = new TextField();
        HBox hbox2 = new HBox(amountLabel, amountTextField);
        hbox2.setAlignment(Pos.CENTER);

        // Unit
        Label unitLabel = new Label("Unit:");
        unitLabel.setPrefWidth(150);
        unitLabel.setAlignment(Pos.CENTER_RIGHT);
        unitLabel.setPadding(new Insets(0, 5, 0, 0));
        AutoCompletionTextField unitTextField = new AutoCompletionTextField();
        unitTextField.getEntries().addAll(Unit.units);
        HBox hbox3 = new HBox(unitLabel, unitTextField);
        hbox3.setAlignment(Pos.CENTER);

        dialogPane.setContent(new VBox(8, hbox1, hbox2, hbox3));
        Platform.runLater(nameTextField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new AddIngredientResult(nameTextField.getText(),
                        amountTextField.getText(), unitTextField.getText());
            }
            return null;
        });
        Optional<AddIngredientResult> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((AddIngredientResult result) -> {
            addIngredientToList(result.name, result.amount, result.unit);
        });
    }

    @FXML
    public void removeInstructionButtonPressed(ActionEvent event) throws IOException {
        if (selectedInstruction == null) {
            // Alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText(null);
            alert.setContentText("Select an instruction to remove");
            alert.showAndWait();

            return;
        }

        instructionsView.getItems().remove(selectedInstruction);

        instructionsView.getSelectionModel().clearSelection();
        selectedInstruction = null;

        if (instructionsView.getItems().size() == 0) {
            removeInstructionButton.setOpacity(0.3);
            removeInstructionButton.setDisable(true);
        }
    }

    private void addIngredientToList(String name, String amount, String unit) {
        if (isValidInput(name) &&
            isValidInput(amount) &&
            isValidInput(unit)) {
            String s = String.format("%s, %s, %s", name, amount, unit);
            ingredientsView.getItems().add(s);

            removeIngredientButton.setOpacity(1.0);
            removeIngredientButton.setDisable(false);
        }
    }

    @FXML
    public void addInstructionButtonPressed() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Instruction Step");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Instruction Step:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            addInstructionToList(result.get());
        }
    }

    private void addInstructionToList(String s) {
        if (isValidInput(s)) {
            // Add to listView
            instructionsView.getItems().add(s);

            removeInstructionButton.setOpacity(1.0);
            removeInstructionButton.setDisable(false);
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
            System.out.println("instr: " + s);
            recipe.addInstruction(s);
        }
    }

    private boolean isValidInput(String s) {
        if (s.isEmpty()) {
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

    private static class AddIngredientResult {

        String name, amount, unit;

        public AddIngredientResult(String name, String amount, String unit) {
            this.name = name;
            this.amount = amount;
            this.unit = unit;
        }
    }

    @FXML
    public void mouseEnteredRecipeImage() {
        editRecipeImageButton.setVisible(true);
    }

    @FXML
    public void mouseExitedRecipeImage() {
        editRecipeImageButton.setVisible(false);
    }

    @FXML
    public void mouseEnteredEditRecipeImage() {
        editRecipeImageButton.setVisible(true);
    }

    @FXML
    public void mouseExitedEditRecipeImage() {
        editRecipeImageButton.setVisible(false);
    }

    @FXML
    public void editRecipeImageButtonPressed() {
        TextInputDialog dialog = new TextInputDialog(imageURL);
        dialog.setTitle("Change Recipe Image");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Recipe Image URL:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if (isValidInput(result.get())) {
                try {
                    recipeImageView.setImage(new Image(result.get()));
                    imageURL = result.get();
                } catch (Exception e) {
                    // Alert
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("");
                    alert.setHeaderText(null);
                    alert.setContentText("Image URL Invalid");
                    alert.showAndWait();
                }
            }
        }
    }

    @Override
    public void backButtonPressed(ActionEvent event) throws IOException {
        currentRecipe = null;
        super.backButtonPressed(event);
    }
}
