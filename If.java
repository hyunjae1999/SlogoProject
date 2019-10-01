package model.command.control;

import model.Model;
import model.command.Command;

/**
 * A command that represents an if statement block, executing the second parameter if the first is nonzero.
 *
 * @author Hyunjae Lee
 */
public class If extends Command {

    private static final int ARG_COUNT = 2;

    public If() {
        super();
        argCount = ARG_COUNT;
    }

    @Override
    public double execute(Model model) {
        double returnValue = 0;
        if (parameters.get(0).execute(model) != 0) {
            returnValue = parameters.get(1).execute(model);
        }
        return returnValue;
    }

}
