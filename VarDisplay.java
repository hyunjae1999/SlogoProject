package GUI.TabbedGUI.Tabs.Windows.WindowOptions;

import GUI.TabbedGUI.Tabs.Windows.Window;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Result;

import java.util.List;

public class VarDisplay extends Window {

    private static String CSS_FILE = "WindowOptions.css";
    private static final String VARIABLE_DISPLAY = "Variable Display";

    private VBox items;

    /**
     * Window which displays instantiated variables.
     * @param width Width of the window
     * @param height Height of the window
     * @param commandText StringProperty to be changed on submission.
     * @author Hyunjae Lee
     */
    public VarDisplay(double width, double height, StringProperty commandText){
        super(width, height, commandText);
        this.getStylesheets().add(getClass().getResource(CSS_FILE).toExternalForm());
        items = new VBox();
        getChildren().add(items);
        Text t = new Text(VARIABLE_DISPLAY);
        items.getChildren().add(t);
        setColumnIndex(t, 0);
        setRowIndex(t, 0);
    }

    public void addResult(Result result){
        List<Node> children = items.getChildren();
        children.clear();
        children.add(new Text(VARIABLE_DISPLAY));
        if(!result.getVariables().isEmpty()) {
            for (String var : result.getVariables().keySet()) {
                Text t = new Text(var + "=" + result.getVariables().get(var));
                children.add(t);
            }
        }
    }
}
