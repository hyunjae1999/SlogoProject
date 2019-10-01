package model.command;

import java.util.function.BiConsumer;

/**
 * A special type of abstract commands that deals with coordinates of turtles.
 *
 * @author Hyunjae Lee
 */
public abstract class CoordinateCommand extends Command {

    protected Command[] coordinates(int i, int j) {
            var xCom = this.parameters.get(i);
            var yCom = this.parameters.get(j);
            if (xCom == null || yCom == null) {
                return null;
            }
            return new Command[] {xCom, yCom};
        }

    protected void useCor(BiConsumer<Command, Command> command) {
        for(int i = 0; i < this.parameters.size() - 1; i++) {
            for(int j = 1; j < this.parameters.size(); j++) {
                var coordinates = coordinates(i, j);
                if(coordinates == null) {
                    return;
                }
                var xCor = coordinates[0];
                var yCor = coordinates[1];
                command.accept(xCor, yCor);
                i++;
                j++;
            }
        }
    }

}
