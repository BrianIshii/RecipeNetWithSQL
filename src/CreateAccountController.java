import entity.Entity;
import entity.User;
import exception.DuplicateEntryException;
import exception.ExecutorException;
import exception.NoRowsAffectedException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import service.UserService;

import java.io.IOException;
import java.util.regex.Pattern;

public class CreateAccountController {

    @FXML private TextField nameTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField passwordTextField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private Label errorLabel;

    @FXML
    public void createAccountButtonPressed(ActionEvent event) throws IOException {
        // Reset textfields' style
        resetTextFieldsStyle();

        // Check if passwords are the same
        if (isValidInput()) {
            // Create user
            UserService userService = UserService.getInstance();

            User newUser = new User(nameTextField.getText(), emailTextField.getText(), passwordTextField.getText());

            try{
                Entity entity = userService.save(newUser);

                // User create successfully message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText(null);
                alert.setContentText("Account create successfully!");
                alert.showAndWait();

                // Transition to home view
                Main.setUser(newUser);
                Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
                Main.getPrimaryStage().getScene().setRoot(root);
            } catch (DuplicateEntryException dee) {
                // Credentials were not unique
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("");
                alert.setHeaderText(null);
                alert.setContentText("Credentials already exist.");
                alert.showAndWait();
            } catch (NoRowsAffectedException rnaf) {
                // May delete this branch if you want same behavior as ExecutorException
            } catch(ExecutorException ee) {
                // Something went wrong
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("");
                alert.setHeaderText(null);
                alert.setContentText("Something went wrong");
                alert.showAndWait();
            }
        } else {
            System.out.println("User create account failed");
        }
    }

    @FXML
    public void backButtonPressed(ActionEvent event) throws IOException {
        // Transition to main view
        Parent root = FXMLLoader.load(getClass().getResource("MainController.fxml"));
        Main.getPrimaryStage().getScene().setRoot(root);
    }

    private boolean isValidInput() {
        // Check empty fields
        if (nameTextField.getText().isEmpty() ||
            emailTextField.getText().isEmpty() ||
            passwordTextField.getText().isEmpty()) {
            errorLabel.setText("Field(s) cannot be empty");
            errorLabel.setVisible(true);

            if (nameTextField.getText().isEmpty()) alertTextField(nameTextField);
            if (emailTextField.getText().isEmpty()) alertTextField(emailTextField);
            if (passwordTextField.getText().isEmpty()) alertTextField(passwordTextField);

            return false;
        }

        // Check valid email
        if (!isValidEmail(emailTextField.getText())) {
            errorLabel.setText("Email not valid");
            errorLabel.setVisible(true);

            alertTextField(emailTextField);

            return false;
        }

        // Check password match
        if (passwordTextField.getText().compareTo(confirmPasswordTextField.getText()) != 0) {
            errorLabel.setText("Passwords don't match");
            errorLabel.setVisible(true);

            alertTextField(passwordTextField);
            alertTextField(confirmPasswordTextField);

            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private void alertTextField(TextField textField) {
        textField.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
    }

    private void resetTextFieldsStyle() {
        nameTextField.setStyle("");
        emailTextField.setStyle("");
        passwordTextField.setStyle("");
        confirmPasswordTextField.setStyle("");

        errorLabel.setVisible(false);
    }
}
