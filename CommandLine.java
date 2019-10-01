package GUI.TabbedGUI.Tabs.Windows.CommandWindows;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

/**
 * Command line which can be typed in to enter commands.
 * Press F1 to submit a command, or hit the submit button.
 * @author Hyunjae Lee
 */
public class CommandLine extends HBox {

    private static final double WIDTH_RATIO = 0.9;
    private static final int HBOX_SPACING = 10;
    private static final String CSS_FILE = "CommandHistory.css";
    StringProperty currentCommandText;


    /**
     * Instantiate a new CommandLine object.
     * @param width width
     * @param height height
     * @param commandText StringProperty object to be modified to submit a command.
     * @param visualText StringProperty which will contain the current text being displayed on the comand line.
     */
    public CommandLine(double width, double height, StringProperty commandText, StringProperty visualText) {
            this.getStylesheets().add(getClass().getResource(CSS_FILE).toExternalForm());
        currentCommandText = commandText;
        var commandPrompt = commandField(width*WIDTH_RATIO,height);
        commandPrompt.textProperty().bindBidirectional(visualText);
        Button submitButton = makeSubmitButton(width*(1-WIDTH_RATIO), height, commandText);
        getChildren().addAll(commandPrompt, submitButton);
        setWidth(width);
        setHeight(height);
        setSpacing(HBOX_SPACING);
    }

    private TextArea commandField(double width, double height) {
        TextArea text = new TextArea();
        text.setPrefSize(width, height);
        text.textProperty().addListener(event -> text.autosize());
        text.setWrapText(true);
        currentCommandText = text.textProperty();
        return text;
    }

    private Button makeSubmitButton(double totalWidth, double totalHeight, StringProperty commandText) {
        var result = new Button("Submit");
        result.setMaxSize(totalWidth, totalHeight);
        result.setPrefSize(totalWidth, totalHeight);
        result.setOnMouseClicked(event -> submit(commandText));
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F1) {
                submit(commandText);
            }
        });
        return result;
    }

    private void submit(StringProperty commandText) {
        commandText.setValue(currentCommandText.getValue());
        currentCommandText.setValue("");
    }


}
