package model.command.control;

import model.Model;
import model.command.Command;
import model.command.Error;
import model.command.ValueCommand;

import java.util.ResourceBundle;

/**
 * A command that makes a new variable with its name and value, and checks for errors.
 *
 * @author Hyunjae Lee
 */
public class MakeVariable extends Command {

    private static final int ARG_COUNT = 2;

    public MakeVariable() {
        super();
        argCount = ARG_COUNT;
    }

    @Override
    public double execute(Model model) {
        if (!(this.parameters.get(0) instanceof ValueCommand)) {
            parameters.add(new Error(ResourceBundle.getBundle("Error").getString("MalformedMakeVariable")));
            return 0;
        }
        var variable = ((ValueCommand<String>) this.parameters.get(0)).getValue();
        var value = this.parameters.get(1).execute(model);
        model.getVariables().put(variable, value);
        return value;
    }

}
