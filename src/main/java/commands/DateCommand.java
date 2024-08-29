package commands;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateCommand implements Command {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String execute(long chatId, String[] args) {
        return "Data atual: " + LocalDate.now().format(fmt);
    }
}
