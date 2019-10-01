package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Result object that is passed back to the front end, containing all relevant information
 * for the front end to update its display. All information is a copy, so the front-end cannot
 * change the back end data accidentally.
 *
 * @author Luke Truitt
 * @author Hyunjae Lee
 */
public class Result {

    private Map<Integer, ImmutableTurtle> turtles;
    private String commandString, returnString;
    private Color backgroundColor;
    private Map<String, Double> variables;
    private ArrayList<String> userCommands;
    private boolean error;

    /**
     * Creates a result object from the given model, copying all relevant fields.
     * @param model the model from which to create this result
     */
    public Result(Model model) {
        turtles = model.getTurtles().getImmutableTurtles();
        backgroundColor = model.getBackgroundColor();
        variables = new HashMap<>(model.getVariables());
        userCommands = new ArrayList<>(model.getUserInstructions().keySet());
    }

    /**
     * Creates a result object from the given model and includes two strings and an error flag.
     * @param model the model from which to create this result
     * @param commandString the original command string that the user submitted
     * @param returnString the return values in string form (or could be an error message)
     * @param error whether or not there was an error in command execution
     *              (and thus the return string is an error message)
     */
    public Result(Model model, String commandString, String returnString, boolean error) {
        this(model);
        this.commandString = commandString;
        this.returnString = returnString;
        this.error = error;
    }

    /**
     * Get the turtles in the environment.
     * @return a map of turtle ids to their immutable counterparts
     */
    public Map<Integer, ImmutableTurtle> getTurtles() {
        return turtles;
    }

    /**
     * Gets the original submitted command.
     * @return the original submitted command
     */
    public String getCommandString() {
        return commandString;
    }

    /**
     * Gets the return string (or error message) from command execution.
     * @return the return string
     */
    public String getReturnString() {
        return returnString;
    }

    /**
     * Gets the background color for the environment.
     * @return the background color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Gets map of variables that exist in the environment.
     * @return the map of variables from name to value
     */
    public Map<String, Double> getVariables() {
        return variables;
    }

    /**
     * Gets the list of user commands available in the environment.
     * @return a list of command names as strings
     */
    public List<String> getUserCommands() {
        return List.copyOf(userCommands);
    }

    /**
     * Indicates whether there was an error in command execution.
     * @return true if there was an error, false otherwise
     */
    public boolean isError() {
        return error;
    }
}
