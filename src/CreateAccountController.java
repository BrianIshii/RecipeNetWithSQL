
import Entity.User;
import Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateAccountController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField confirmPasswordTextField;

    @FXML
    public void createAccountButtonPressed(ActionEvent event) throws IOException {
        // Check if passwords are the same
        if (passwordTextField.getText().compareTo(confirmPasswordTextField.getText()) == 0) {
            // Create user
            UserService userService = UserService.getInstance();

            userService.save(new User(nameTextField.getText(), emailTextField.getText(), passwordTextField.getText()));

            // Transition to home view
            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Main.getPrimaryStage().getScene().setRoot(root);
        } else {
            // Alarm that the passwords do not match
            System.out.println("Passwords don't match");
        }
    }

    @FXML
    public void backButtonPressed(ActionEvent event) throws IOException {
        // Transition to main view
        Parent root = FXMLLoader.load(getClass().getResource("MainController.fxml"));
        Main.getPrimaryStage().getScene().setRoot(root);
    }
}
