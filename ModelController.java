package model;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import model.Events.ResultEvent;
import model.command.Command;
import model.command.Error;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * This main model controller class for executes commands passed to it from the
 * main controller. It also notifies any interested parties with the result of
 * executed commands.
 *
 * @author Hyunjae Lee
 * @author Sam Rabinowitz
 */
public class ModelController {

    private static final String SAVE_COMMAND_HISTORY = "saveCommandHistory";
    private static final String LOAD_COMMAND_HISTORY = "loadCommandHistory";
    private static final String CHANGE_LANGUAGE = "changeLanguage";

    private Model model;
    private Node node;
    private List<String> commandHistory;
    private CommandParser parser;

    /**
     * Create the model controller, initializing all relevant instance variables.
     */
    public ModelController() {
        model = new Model();
        node = new Rectangle();
        commandHistory = new ArrayList<>();
        parser = new CommandParser();
    }

    /**
     * Executes a given command string passed from the GUI.
     * First it checks whether this is a special command for loading/saving command history
     * or setting the language. If so, it processes those in certain ways and sets no return string.
     * Otherwise, it uses the command parser to get a list of command roots.
     * It then loops through those roots and traverses the trees, executing commands and getting the return string.
     * It also loops through the trees again to check for errors.
     * Lastly, it fires a ResultEvent with a Result object that has all the necessary data from the model
     * packaged up nicely and immutably for the front end to update its display.
     *
     * @param command the unprocessed command from the GUI
     */
    public void execute(String command) {
        int saveLoadResult = checkSaveLoadHistory(command);
        if (saveLoadResult != 0) {
            String success = ResourceBundle.getBundle("Error").getString("CommandHistorySuccess");
            String error = ResourceBundle.getBundle("Error").getString("CommandHistoryError");
            fireResult(new String[] {command, success, saveLoadResult == 1 ? "" : error});
            return;
        }

        if (command.startsWith(CHANGE_LANGUAGE)) {
            parser.setLanguage(command.split(" ")[1]);
            return;
        }

        commandHistory.add(command);
        var commandRoots = parser.parse(command, model);
        fireResult(new String[] {command, generateReturn(commandRoots), commandTreeContainsError(commandRoots)});
    }

    private void fireResult(String[] params) {
        Result result = new Result(model, params[0], !params[2].equals("") ? params[2] : params[1], !params[2].equals(""));
        node.fireEvent(new ResultEvent(result));
    }

    private String generateReturn(List<Command> commandRoots) {
        var returnString = new StringBuilder();
        for(Command c : commandRoots) {
            double returnValue = c.execute(model);
            returnString.append(" " + returnValue);
        }
        return returnString.toString();
    }

    private String commandTreeContainsError(List<Command> commands) {
        for (Command c : commands) {
            if (c instanceof Error) {
                return c.toString();
            }
            String childError = commandTreeContainsError(c.getParameters());
            if (!childError.equals("")) {
                return childError;
            }
        }
        return "";
    }

    /**
     * Adds a listener that is triggered when a command's result is
     * generated and allows for retrieving that result.
     *
     * @param eventHandler the listener for the result
     */
    public void addResultHandler(EventHandler<? super ResultEvent> eventHandler) {
        node.addEventFilter(ResultEvent.NEW_RESULT, eventHandler);
    }

    // Returns 0 for not applicable, 1 for success, -1 for error
    private int checkSaveLoadHistory(String command) {
        if (command.startsWith(SAVE_COMMAND_HISTORY)) {
            return saveCommandHistory(command.split(" ")[1]) ? 1 : -1;
        }
        else if (command.startsWith(LOAD_COMMAND_HISTORY)) {
            return loadCommandHistory(command.split(" ")[1]) ? 1 : -1;
        }
        return 0;
    }

    /**
     * Saves the command history to a text file ending in `.logovar` to be easily identifiable.
     * From https://stackoverflow.com/questions/2885173/how-do-i-create-a-file-and-write-to-it-in-java
     *
     * @param filePath the absolute path of the file, with no spaces and optional extension
     * @return true if the command history was successfully saved, false if not
     */
    private boolean saveCommandHistory(String filePath) {
        filePath += !filePath.endsWith(".logovar") ? ".logovar" : "";
        Path file = Paths.get(filePath);
        try {
            Files.write(file, commandHistory, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    /**
     * Loads command history from a text file ending in `.logovar` and executes it.
     * From https://stackoverflow.com/questions/3402735/what-is-simplest-way-to-read-a-file-into-string
     *
     * @param filePath the absolute path of the file, with no spaces
     * @return true if the command history was successfully loaded, false if not
     */
    private boolean loadCommandHistory(String filePath) {
        try {
            String content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
            execute(content);
        } catch (FileNotFoundException e ) {
            System.out.println(e);
            return false;
        }
        return true;
    }
}
