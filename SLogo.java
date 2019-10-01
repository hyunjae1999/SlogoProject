package Controller;


import GUI.GUIFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import model.ModelController;

import java.util.HashMap;
import java.util.Map;

import static GUI.Events.CloseEvent.CLOSE;
import static GUI.Events.OpenEvent.OPEN;
import static GUI.Events.SubmitEvent.SUBMIT;


/**
 * This is the main controller class for the Controller.SLogo project.
 * To launch the window, call the main method of this class.
 *
 */
public class SLogo extends Application {

    private Map<String,ModelController> models = new HashMap<>();
    private static final String TITLE = "SLogo";

    /**
     * In this method the initial GUI window is created, and listeners are added to create or close ModelController instances
     * when tab events are fired.
     * @param stage the stage for the JavaFX application
     */
    @Override
    public void start(Stage stage) {
        var gui = GUIFactory.makeInstance(stage);
        stage.setTitle(TITLE);
        stage.show();
        gui.addGUIEventFilter(CLOSE, event -> models.remove(event.getTabID()));
        gui.addGUIEventFilter(OPEN, event -> {
            if (models.containsKey(event.getTabID()))
                return;
            var model = new ModelController();
            model.addResultHandler(event2 -> gui.addResult(event2.getResult(), event.getTabID()));
            models.put(event.getTabID(), model);
            model.execute("ct"); // create the first turtle
        });
        gui.addGUIEventFilter(SUBMIT, event -> models.get(event.getTabID()).execute(event.getCommandString()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
