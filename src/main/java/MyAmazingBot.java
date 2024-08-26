import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final Map<String, Command> commands;
    private final Map<Long, Boolean> userStarted;

    public MyAmazingBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
        commands = new HashMap<>();
        userStarted = new HashMap<>();
        registerCommands();
        setBotCommands(); // Configura os comandos no Telegram
    }

    private void registerCommands() {
        commands.put("/start", new StartCommand(userStarted));
        commands.put("/help", new HelpCommand(commands));
        commands.put("/date", new DateCommand());
        commands.put("/time", new TimeCommand());
        commands.put("/echo", new EchoCommand());
        commands.put("/random", new RandomCommand());
    }

    private void setBotCommands() {
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "Inicia a conversa com o bot"));
        commandList.add(new BotCommand("/help", "Lista todos os comandos disponíveis"));
        commandList.add(new BotCommand("/date", "Mostra a data atual"));
        commandList.add(new BotCommand("/time", "Mostra a hora atual"));
        commandList.add(new BotCommand("/echo", "Repete a mensagem enviada"));
        commandList.add(new BotCommand("/random", "Gera um número aleatório"));

        SetMyCommands setMyCommands = SetMyCommands.builder()
                .commands(commandList)
                .scope(new BotCommandScopeDefault())
                .build();

        try {
            telegramClient.execute(setMyCommands);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Erro ao definir comandos do bot: ", e);
        }
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/")) { // Se a mensagem é um comando
                String[] parts = messageText.split(" ");
                String command = parts[0];
                String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

                if (commands.containsKey(command)) {
                    commands.get(command).execute(chatId, args);
                } else {
                    sendMessage(chatId, "Comando não reconhecido: " + messageText);
                }
            } else { // Se a mensagem é texto normal
                if (Boolean.TRUE.equals(userStarted.get(chatId))) {
                    handleMessage(chatId, messageText);
                } else {
                    sendMessage(chatId, "Por favor, envie /start para começar.");
                }
            }
        }
    }

    private void handleMessage(long chatId, String messageText) {
        sendMessage(chatId, "Você disse: " + messageText);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Erro ao enviar mensagem: ", e);
        }
    }
//--------------------------- Interface + implementação de cada comando----------------------------
    // Interface Command
    @FunctionalInterface
    private interface Command {
        void execute(long chatId, String[] args);
    }

    // Implementação do comando /start
    private class StartCommand implements Command {
        private final Map<Long, Boolean> userStarted;

        public StartCommand(Map<Long, Boolean> userStarted) {
            this.userStarted = userStarted;
        }

        @Override
        public void execute(long chatId, String[] args) {
            userStarted.put(chatId, true);
            sendMessage(chatId, "Bem-vindo ao bot! Agora você pode enviar mensagens.");
        }
    }

    // Implementação do comando /help
    private class HelpCommand implements Command {
        private final Map<String, Command> commands;

        public HelpCommand(Map<String, Command> commands) {
            this.commands = commands;
        }

        @Override
        public void execute(long chatId, String[] args) {
            StringBuilder helpMessage = new StringBuilder("Comandos disponíveis:\n");
            for (String command : commands.keySet()) {
                helpMessage.append(command).append("\n");
            }
            sendMessage(chatId, helpMessage.toString());
        }
    }

    // Implementação do comando /date
    private class DateCommand implements Command {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        @Override
        public void execute(long chatId, String[] args) {
            sendMessage(chatId, "Data atual: " + LocalDate.now().format(fmt));
        }
    }

    // Implementação do comando /time
    private class TimeCommand implements Command {
        @Override
        public void execute(long chatId, String[] args) {
            sendMessage(chatId, "Hora atual: " + LocalTime.now().toString());
        }
    }

    // Implementação do comando /echo
    private class EchoCommand implements Command {
        @Override
        public void execute(long chatId, String[] args) {
            if (args.length > 0) {
                sendMessage(chatId, String.join(" ", args));
            } else {
                sendMessage(chatId, "Por favor, forneça uma mensagem para repetir.");
            }
        }
    }

    // Implementação do comando /random
    private class RandomCommand implements Command {
        @Override
        public void execute(long chatId, String[] args) {
            int randomNumber = new Random().nextInt(100) + 1; // Número aleatório entre 1 e 100
            sendMessage(chatId, "Número aleatório: " + randomNumber);
        }
    }
}
