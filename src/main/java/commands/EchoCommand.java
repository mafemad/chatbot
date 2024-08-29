package commands;

public class EchoCommand implements Command {

    @Override
    public String execute(long chatId, String[] args) {
        if (args.length > 0) {
            return String.join(" ", args);
        } else {
            return "Por favor, forneça uma mensagem para repetir.";
        }
    }
}
