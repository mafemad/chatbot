package commands;

import java.util.Map;

public class HelpCommand implements Command {
    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public String execute(long chatId, String[] args) {
        StringBuilder helpMessage = new StringBuilder("Comandos disponíveis:\n");
        for (String command : commands.keySet()) {
            helpMessage.append(command).append("\n");
        }
        return helpMessage.toString();
    }
}
