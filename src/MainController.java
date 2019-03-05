
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

    @FXML
    public void loginButtonPressed(ActionEvent event) throws IOException {
        emailTextField.setText("1");
        passwordTextField.setText("2");
    }

    @FXML
    public void createAccountPressed(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("CreateAccountController.fxml"));
        Main.getPrimaryStage().getScene().setRoot(root);
    }
}
