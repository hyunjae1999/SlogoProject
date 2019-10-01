package model;

import model.command.Command;
import model.command.Error;
import model.command.ValueCommand;
import model.command.control.UserCommand;
import model.command.control.group.ListBody;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * This class is responsible for converting command strings into command objects.
 * Taking the long input and breaking it up by spaces, then determines what each
 * individual string translates to and builds a tree of commands that will
 * be executed after.
 *
 * @author Hyunjae Lee
 */
public class CommandParser {

    public static final String WHITESPACE = "\\s+";
    public static final String CONSTANT = "model.command.math.Constant";
    public static final String VARIABLE = "model.command.control.Variable";
    public static final String LIST_START = "model.command.control.group.ListStart";
    public static final String GROUP_START = "model.command.control.group.GroupStart";
    public static final String LIST_END = "model.command.control.group.ListEnd";
    public static final String GROUP_END = "model.command.control.group.GroupEnd";
    public static final String VALUE_COMMAND = "model.command.ValueCommand";
    public static final String USER_COMMAND = "model.command.control.UserCommand";
    /**
     * Language paths.
     */
    public static final String LANGUAGE = "language";
    public static final String ERRORS = "Error";
    public static final String SYNTAX = "languages/Syntax";
    public static final String TRANSLATION = "languages/Translation";

    private List<Command> commands;
    private Model model;
    private List<Map.Entry<String, String>> errors;
    List<Map.Entry<String, Pattern>> translations;
    List<Map.Entry<String, Pattern>> commandTranslations;

    /**
     * Sets default language, initializes other parameters
     */
    public CommandParser(){
        setLanguage("ENGLISH");
        commandTranslations = getPatterns(new String[] {TRANSLATION});
        commands = new ArrayList<>();
        addErrors();
    }

    /**
     * @param input the string with all potential commands
     * @param model current Model of the game
     * @return The list of executable commands
     */
    public List<Command> parse(String input, Model model) {
        this.model = model;
        this.commands = new ArrayList<>();
        parseText(input);
        return fillRoot();
    }

    /**
     * Set the parser's language based on indicated key in the language.properties file.
     * Always includes the Syntax language.
     * @param languageKey the key for the path to the language file
     */
    public void setLanguage(String languageKey) {
        String[] languages = {ResourceBundle.getBundle(LANGUAGE).getString(languageKey), SYNTAX};
        translations = getPatterns(languages);
    }

    /**
     * Based on parseText by @Robert Duvall
     * Takes a generic command and parses it into an array of Command
     * objects for execution.
     *
     * @param input the generic command string
     */

    private void parseText (String input) {

        var inputArray = input.split(WHITESPACE);
        for (int i = 0; i < inputArray.length; i++){
            String s = inputArray[i];
            if (s.trim().length() <= 0) {
                continue;
            }
            i = execute(s, i, inputArray);
        }
    }

    /**
     * Runs through all of the commands in the input array to determine the appropriate action
     * and take it
     * @param s current string to be analyzed
     * @param i index through the command string
     * @param inputArray array of the command strings
     * @return the updated index
     */
    private int execute(String s, int i, String[] inputArray) {
        try {
            i = addCommand(getCommand(s), s, i, inputArray);
        } catch (Exception first) {
            try {
                i = addCommand(addUserCommand(s), s, i, inputArray);
            } catch (Exception second) {
                commands.add(new Error(String.format(getError("CommandNotFound"), s)));
                System.err.println(first + " " + second);
            }
        }
        return i;
    }

    /**
     * @param command the command we are working on now
     * @param s current string
     * @param i current index through the array
     * @param inputArray array of strings inputted
     * @return updated index
     */
    private int addCommand(Command command, String s, int i, String[] inputArray) {
        if(command.getClass().getName().equals(CONSTANT)) {
            ((ValueCommand) command).setValue(Double.parseDouble(s)); }
        else if (command.getClass().getName().equals(VARIABLE)) {
            ((ValueCommand) command).setValue(s); }
        else if (command.getClass().getSuperclass().getName().equals(VALUE_COMMAND)) {
            i++;
            s = inputArray[i];
            if (checkExisting(s)) {
                command = new Error(String.format(getError("ExistingCommand"), s));
                i = inputArray.length;
            } else { ((ValueCommand) command).setValue(s); }
        }
        this.commands.add(command);
        return i;
    }

    /**
     *
     * @param s user command title
     * @return Filled in user command
     * @throws Exception if user command does not exist
     */
    private Command addUserCommand(String s) throws Exception {
        Command[] userCommand = retrieveUserCommand(s);
        UserCommand command = new UserCommand();
        command.addParameter(userCommand[0]);
        command.addParameter(userCommand[1]);
        return command;
    }

    /**
     *
     * @param text command to find in user command list
     * @return the pair of params for the user commmand
     * @throws Exception if there is no corresponding user command
     */
    private Command[] retrieveUserCommand(String text) throws Exception {
        if(this.model.getUserInstructions().containsKey(text)) {
            return this.model.getUserInstructions().get(text);
        }
        throw new Exception("Command " + text + " not found");
    }

    /**
     * Uses reflection to change the incoming string into a Command object
     * @param s Incoming string
     * @return Command object
     * @throws Exception if the string doesn't translate into a command
     */
    private Command getCommand(String s) throws Exception {
        Class c = Class.forName("model.command." + this.getSymbol(this.getSymbol(s, translations), commandTranslations));
        Constructor ct = c.getConstructor();
        return (Command) ct.newInstance();
    }

    /**
     * @param s prospective user command
     * @return whether or not the user command is going to overwrite an existing command
     */
    private boolean checkExisting(String s) {
        return this.getSymbol(this.getSymbol(s, translations), commandTranslations) != "" || !this.getSymbol(this.getSymbol(s, translations), commandTranslations).equals("");
    }

    /**
     * Updates the map of possible errors
     */
    private void addErrors() {
        this.errors = new ArrayList<>();
        var resources = ResourceBundle.getBundle(ERRORS);
        for (var key : Collections.list(resources.getKeys())) {
            var value = resources.getString(key);
            errors.add(new AbstractMap.SimpleEntry<>(key, value));
        }
    }

    /**
     * @Author Robert Duvall
     * Loads up all the pattern files from the data folder.
     */
    private List<Map.Entry<String, Pattern>> getPatterns(String[] languages) {
        List<Map.Entry<String, Pattern>> translations = new ArrayList<>();
        for(var language : languages) {
            translations = addPatterns(language, translations);
        }
        return translations;
    }

    /**
     * for each file passed in, creates a map entry for each entry
     * in the file.
     * From @Robert Duvall
     */
    private List<Map.Entry<String, Pattern>>  addPatterns (String syntax, List<Map.Entry<String, Pattern>> translations) {
        var resources = ResourceBundle.getBundle(syntax);
        for (var key : Collections.list(resources.getKeys())) {
            var regex = resources.getString(key);
            translations.add(new AbstractMap.SimpleEntry<>(key,
                    Pattern.compile(regex, Pattern.CASE_INSENSITIVE)));
        }
        return translations;
    }

    /**
     * Based on getSymbol by @Author: Robert Duvall
     * @param text command from User
     * @param translations the set of translations to compare the key against
     * @return The command as a recognizable string
     */
    private String getSymbol(String text, List<Map.Entry<String, Pattern>> translations) {
        for (var e : translations) {
            if (match(text, e.getValue())) {
                return e.getKey();
            }
        }
        return "";
    }

    /**
     * Translates error key into a readable string
     * @param text key to an error
     * @return Error Result string
     */
    private String getError(String text) {
        for (var e : errors) {
            if (text.equals(e.getKey())) {
                return e.getValue();
            }
        }
        return "";
    }

    /**
     * Method that increments through the command list and calls on the
     * builder method to continue building the tree.
     * @return
     */
    private Command getCommandTree() {
        var command = this.commands.get(0);
        var comSize = commands.size();
        commands = commands.subList(1, comSize);
        return builder(command);
    }

    /**
     * Filters the type of command.
     * @param command next Command to build on
     * @return the update Command
     */
    private Command builder(Command command) {
        if (command.getArgCount() == 0) {
            return command; }
        else if (command.getClass().getName().equals(USER_COMMAND)) {
            return createUserCommand(command); }
        else if (command.getClass().getName().equals(LIST_START)) {
            return groupBuilder(new ListBody(), LIST_END); }
        else if (command.getClass().getName().equals(GROUP_START)) {
            command = this.commands.get(0);
            this.commands = this.commands.subList(1, this.commands.size());
            return groupBuilder(command, GROUP_END); }
        else if (this.commands.size() < command.getArgCount()) {
            return new Error("Not enough parameters for command " + command.toString()); }
        else {
            for (int i = 0; i < command.getArgCount(); i++) {
                command.addParameter(getCommandTree());
            }
            return command;
        }
    }

    /**
     * For the Group objects, there is a slightly different method for building t
     * the command tree. This method addresses those differences
     * @param command The command that is going to be build on
     * @param end determines which type of Group element the groupBuilder needs
     *            to look for
     * @return The new command with a command tree of parameters
     */
    private Command groupBuilder(Command command, String end) {
        while(!this.commands.get(0).getClass().getName().equals(end)){
            command.addParameter(getCommandTree());
        }
        this.commands = this.commands.subList(1, commands.size());
        return command;
    }

    /**
     * If the user is trying to create their own command, this method parses it.
     * @param command is the Command object that is a user command
     * @return the updated UserCommand that has correctly filled it's parameters
     */
    private Command createUserCommand(Command command) {
        int count = command.getParameters().get(0).getParameters().size();
        var param = new ListBody();
        while(count > 0) {
            param.addParameter(getCommandTree());
            count--;
        }
        command.addParameter(param);
        return command;
    }

    /**
     * Fills the list of commands while there are still commands to add
     * @return the new list Commands to be executed by the Controller
     */
    private ArrayList<Command> fillRoot() {
        var rootList = new ArrayList<Command>();
        while(!commands.isEmpty()) {
            rootList.add(getCommandTree());
        }
        return rootList;
    }

    /**
     * From file by @Robert Duvall
     * @param text text to check against
     * @param regex pattern to compare to
     * @return whether or not the text matches the given pattern
     */
    private boolean match (String text, Pattern regex) {
        return regex.matcher(text).matches();
    }

}
