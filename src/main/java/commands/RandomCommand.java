package commands;

import java.util.Random;

public class RandomCommand implements Command {

    @Override
    public String execute(long chatId, String[] args) {
        int randomNumber = new Random().nextInt(100) + 1; // Número aleatório entre 1 e 100
        return "Número aleatório: " + randomNumber;
    }
}
