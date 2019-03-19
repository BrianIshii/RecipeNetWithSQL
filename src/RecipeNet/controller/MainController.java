package RecipeNet.controller;

import RecipeNet.Main;
import RecipeNet.entity.User;
import RecipeNet.exception.EntityNotFoundException;
import RecipeNet.exception.ExecutorException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import RecipeNet.service.DatabaseConnection;
import RecipeNet.service.UserService;

import java.io.IOException;

public class MainController {
    DatabaseConnection connection = DatabaseConnection.getInstance();
    public static String FXML = "MainController.fxml";

    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;

    private UserService userService = UserService.getInstance();

    @FXML
    public void initialize() {
        if (connection.getConnection() != null) {
            System.out.println("working");
        }
    }
    @FXML
    public void loginButtonPressed(ActionEvent event) throws IOException {
        try {
            // Login
            User authenticatedUser = userService.authenticate(emailTextField.getText(), passwordTextField.getText());

            // Transition to home view
            Main.setLoggedInUser(authenticatedUser);
            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Main.getPrimaryStage().getScene().setRoot(root);
            root.requestFocus();
        } catch (EntityNotFoundException enfe) {
            // Alarm that the credentials are not valid
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText(null);
            alert.setContentText("Credentials are not valid");
            alert.showAndWait();
        } catch (ExecutorException ee) {
            ee.printStackTrace();
            //TODO determine behavior
        }
    }

    @FXML
    public void createAccountPressed(ActionEvent event) throws IOException {
        // Transition to create account view
        Parent root = FXMLLoader.load(getClass().getResource("CreateAccountController.fxml"));
        Main.getPrimaryStage().getScene().setRoot(root);
    }
}
