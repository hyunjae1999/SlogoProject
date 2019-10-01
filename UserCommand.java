package model.command.control;

import model.Model;
import model.command.Command;
import model.command.Error;
import model.command.ValueCommand;

import java.util.ResourceBundle;

/**
 * A command that represents a user command.
 * first param is variable ListBody, second param is command ListBody and third param is ListBody of the arguments
 *
 * @author Hyunjae Lee
 * @author Luke Truitt
 */
public class UserCommand extends Command {

    private static final int ARG_COUNT = 3;

    public UserCommand() {
        super();
        this.argCount = ARG_COUNT;
    }

    @Override
    public double execute(Model model) {
        var variables = this.parameters.get(0).getParameters();
        var command = this.parameters.get(1);
        var values = this.parameters.get(2).getParameters();
        for(int i = 0; i < variables.size(); i++) {
            var variable = variables.get(i);
            var value = values.get(i);
            if (!(variable instanceof ValueCommand) || !(value instanceof ValueCommand)) {
                parameters.add(new Error(ResourceBundle.getBundle("Error").getString("MalformedUserCommand")));
            }
            var varName = ((ValueCommand<String>) variable).getValue();
            var val = ((ValueCommand<Double>) value).getValue();
            model.getVariables().put(varName, val);
        }

        return command.execute(model);
    }
}
