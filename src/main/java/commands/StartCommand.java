package commands;

import java.util.Map;

public class StartCommand implements Command {
    private final Map<Long, Boolean> userStarted;

    public StartCommand(Map<Long, Boolean> userStarted) {
        this.userStarted = userStarted;
    }

    @Override
    public String execute(long chatId, String[] args) {
        userStarted.put(chatId, true);
        return "Bem-vindo ao bot! Agora você pode enviar mensagens.";
    }
}
