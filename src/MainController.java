
import Entity.User;
import Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MainController {

    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;

    private UserService userService = UserService.getInstance();

    @FXML
    public void loginButtonPressed(ActionEvent event) throws IOException {
        // Login
        User authenticatedUser = userService.authenticate(emailTextField.getText(), passwordTextField.getText());

        if (authenticatedUser != null) {
            System.out.println(authenticatedUser);

            // Transition to home view
            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Main.getPrimaryStage().getScene().setRoot(root);
        } else {
            // Alarm that the credentials are not valid
            System.out.println("Credentials are not valid");
        }
    }

    @FXML
    public void createAccountPressed(ActionEvent event) throws IOException {
        // Transition to create account view
        Parent root = FXMLLoader.load(getClass().getResource("CreateAccountController.fxml"));
        Main.getPrimaryStage().getScene().setRoot(root);
    }
}
