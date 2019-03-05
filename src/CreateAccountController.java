
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateAccountController {

    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField confirmPasswordTextField;

    @FXML
    public void createAccountButtonPressed(ActionEvent event) {
        emailTextField.setText("1");
        passwordTextField.setText("2");
        confirmPasswordTextField.setText("2");
    }

    @FXML
    public void backButtonPressed(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainController.fxml"));
        Main.getPrimaryStage().getScene().setRoot(root);
    }
}
