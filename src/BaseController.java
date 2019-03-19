import service.RecipeService;
import com.sun.org.apache.regexp.internal.RE;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseController {
    protected RecipeService recipeService = RecipeService.getInstance();

    private static List<String> views = new ArrayList<String>();
    private static int currentViewIndex = 0;
    protected String FXML;

    public BaseController(String FXML) {
//        System.out.println("current index " + currentViewIndex);
//        System.out.println(views.size());

        this.FXML = FXML;
    }

    private void changeView(String filename) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(filename));
            Main.getPrimaryStage().getScene().setRoot(root);
            root.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void changeViewTo(String filename) {
        views.add(currentViewIndex, this.FXML);
        currentViewIndex++;
        changeView(filename);
    }

    private void previousView() {
        if (currentViewIndex == views.size()) {
            views.add(currentViewIndex, this.FXML);
        }
        currentViewIndex--;
        changeView(views.get(currentViewIndex));
    }

    private void nextView() {
        currentViewIndex++;
        changeView(views.get(currentViewIndex));
    }

    protected void logout() {
        Main.setUser(null);
        views = new ArrayList<>();
        currentViewIndex = 0;
        changeView(MainController.FXML);
    }

    protected boolean canPressBackButton() {
        return currentViewIndex > 0;
    }

    protected boolean canPressForwardButton() {
        return currentViewIndex < views.size()-1;
    }

    public void backButtonPressed(ActionEvent event) throws IOException {
        previousView();
    }

    public void forwardButtonPressed(ActionEvent event) throws IOException {
        nextView();
    }
}
