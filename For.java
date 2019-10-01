package model.command.control;

import model.Model;
import model.command.Command;
import model.command.Error;
import model.command.ValueCommand;

import java.util.List;
import java.util.ResourceBundle;

/**
 * A command that represents a for loop, and checks for errors.
 *
 * @author Hyunjae Lee
 */
public class For extends Command {

    private static final int ARG_COUNT = 2;
    private static final int FIRST_GROUP_LENGTH = 4;
    private static final int START_LOCATION = 1;
    private static final int END_LOCATION = 2;
    private static final int INCREMENT_LOCATION = 3;

    public For() {
        super();
        argCount = ARG_COUNT;
    }

    @Override
    public double execute(Model model) {
        List<Command> firstGroup = parameters.get(0).getParameters();
        if (firstGroup.size() != FIRST_GROUP_LENGTH || !(firstGroup.get(0) instanceof ValueCommand)) {
            parameters.add(new Error(ResourceBundle.getBundle("Error").getString("MalformedForFirstGroup")));
            return 0;
        }
        String variableName = ((ValueCommand<String>) firstGroup.get(0)).getValue();
        double start = firstGroup.get(START_LOCATION).execute(model);
        double end = firstGroup.get(END_LOCATION).execute(model);
        double increment = firstGroup.get(INCREMENT_LOCATION).execute(model);

        double returnValue = 0;
        for (double i = start; i <= end; i += increment) {
            model.getVariables().put(variableName, i);
            returnValue = parameters.get(1).execute(model);
        }
        return returnValue;
    }

}
