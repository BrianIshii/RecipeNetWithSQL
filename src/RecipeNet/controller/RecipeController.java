package RecipeNet.controller;

import RecipeNet.Main;
import RecipeNet.entity.Ingredient;
import RecipeNet.entity.IngredientRecipe;
import RecipeNet.entity.Instruction;
import RecipeNet.entity.Recipe;
import RecipeNet.exception.EntityNotFoundException;
import RecipeNet.exception.ExecutorException;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import RecipeNet.service.RecipeService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

import java.io.IOException;

public class RecipeController extends BaseController {
    public static String FXML = "Recipe.fxml";
    public static Long recipeID = Long.valueOf(0);
    private Recipe recipe;
    private Boolean isOwner;

    private RecipeService recipeService = RecipeService.getInstance();

    @FXML private Button backButton;
    @FXML private Button forwardButton;
    @FXML ImageView image;
    @FXML ListView<String> ingredientsView = new ListView<>();
    @FXML ListView<String> instructionsView = new ListView<>();
    @FXML Label recipeNameLabel;
    @FXML Button editButton;

    public RecipeController() {
        super(FXML);
    }

    @FXML
    public void initialize() {

        instructionsView.setCellFactory(param -> new ListCell<String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) {
                    setGraphic(null);
                    setText(null);
                    // other stuff to do...
                }else{
                    // set the width's
                    setMinWidth(param.getWidth()-5);
                    setMaxWidth(param.getWidth()-5);
                    setPrefWidth(param.getWidth()-5);

                    // allow wrapping
                    setWrapText(true);

                    setText(item.toString());
                }
            }
        });

        backButton.setDisable(!canPressBackButton());
        forwardButton.setDisable(!canPressForwardButton());

        recipe = null;
        try{
            recipe = recipeService.searchById(recipeID);
            isOwner = Main.userHasPermission((Long)recipe.getFieldValue(Recipe.UID));
            editButton.setVisible(isOwner);
            recipeNameLabel.setText((String)recipe.getFieldValue(Recipe.TITLE));
            String url = (String)recipe.getFieldValue(Recipe.URL);
            if (!url.isEmpty()) {
                Image i = new Image((String)recipe.getFieldValue(Recipe.URL));
                image.setImage(i);
            } else {
                image.setFitHeight(0.0);
            }
        } catch(EntityNotFoundException enfe) {
            //TODO add failure behavior
        } catch (ExecutorException ee) {
            //TODO add failure behavior
        }

        for (IngredientRecipe ir : recipe.getIngredients()) {
            Ingredient i = ir.getIngredient();
            String ingredientInfo = (i.getFieldValue(Ingredient.NAME) + "/" + ir.getField("amount").getValue() + " " + ir.getField("unit").getValue());
            ingredientsView.getItems().add(ingredientInfo);
        }

        for (Instruction i : recipe.getInstructions()) {
            String instructionInfo = (i.getFieldValue(Instruction.STEP) + ". " + i.getFieldValue(Instruction.DESCRIPTION));
            instructionsView.getItems().add(instructionInfo);
        }

        ingredientsView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("selected");
            }
        });

        ingredientsView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) ->
                        System.out.println("Check box for "+item+" changed from "+wasSelected+" to "+isNowSelected)
                );
                return observable ;
            }
        }));

        System.out.println(recipe);
    }

    public void homeButtonPressed(ActionEvent event) throws IOException {
        changeViewTo(HomeController.FXML);
    }

    public void editButtonPressed(ActionEvent event) throws IOException {
        AddRecipeController.currentRecipe = recipe;
        changeViewTo(AddRecipeController.FXML);
    }
}
