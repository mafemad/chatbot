import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import commands.*;

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
        setBotCommands();
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

            if (messageText.startsWith("/")) {
                String[] parts = messageText.split(" ");
                String command = parts[0];
                String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

                if (commands.containsKey(command)) {
                    String message = commands.get(command).execute(chatId, args);
                    sendMessage(chatId, message);
                } else {
                    sendMessage(chatId, "Comando não reconhecido: " + messageText);
                }
            } else {
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
}
