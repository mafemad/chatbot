package commands;

import java.time.LocalTime;

public class TimeCommand implements Command {

    @Override
    public String execute(long chatId, String[] args) {
        return "Hora atual: " + LocalTime.now().toString();
    }
}
