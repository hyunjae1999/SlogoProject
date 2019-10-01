package model.command;

import model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/*
 * Super-class that operates command to return the result of the command.
 *
 * @author Hyunjae Lee
 * @author Samuel Rabinowitz (ssr18)
 */
public abstract class Command {

    protected ArrayList<Command> parameters;
    protected int argCount;

    /**
     * Create a new command and initialize instance variables.
     */
    public Command() {
        parameters = new ArrayList<>();
    }

    /**
     * Executes the command on the model, returning the value of the last sub-command executed.<br>
     * See the JavaDoc for the command's class itself to get the gist of its functionality.
     *
     * @param model the model on which to execute the command
     * @return the result (a double) of the command's execution
     */
    public abstract double execute(Model model);

    /**
     * Add a parameter to the command.
     * @param c the command parameter to add
     */
    public void addParameter(Command c) {
        parameters.add(c);
    }

    /**
     * Get the list of parameters for this command.
     * @return the list of parameters
     */
    public List<Command> getParameters() {
        return List.copyOf(parameters);
    }

    /**
     * Get the number of arguments this command accepts.
     * @return the number of arguments this command accepts
     */
    public int getArgCount() {
        return argCount;
    }

    /**
     * Returns a string representing the command for printing.
     * @return a string representing the command
     */
    @Override
    public String toString() {
        var str = this.getClass().getName();
        str = str.replace(this.getClass().getPackageName() + ".", "");
        var sb = new StringBuilder(str);
        sb.append("\n");
        for(Command param : parameters) {
            sb.append(" -> " + param.toString());
        }
        return sb.toString();
    }

    protected long checkValue(Command param) {
        long color = 0;
        if (param.getClass().getSuperclass().getName().equals("model.command.ValueCommand")) {
            var dub = (double) ((ValueCommand) param).getValue();
            color = (long) dub;
        } else {
            parameters.add(new Error(ResourceBundle.getBundle("Error").getString("MalformedColor")));
            color = -1;
        }
        return color;
    }
}
