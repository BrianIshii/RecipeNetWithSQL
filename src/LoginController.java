import Entity.User;
import Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML private TextField email;
    @FXML private TextField password;
    UserService userService = UserService.getInstance();

    public void pressButton(ActionEvent event) throws IOException {
        System.out.println(email.getText());
        System.out.println(password.getText());

        User authenticatedUser = userService.authenticate(email.getText(), password.getText());

        if (authenticatedUser != null) {
            System.out.println(authenticatedUser);
        }
        Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Main.getPrimaryStage().setScene(new Scene(root, 600, 400));

    }
}
