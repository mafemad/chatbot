package commands;

@FunctionalInterface
public interface Command {
    String execute(long chatId, String[] args);
}
