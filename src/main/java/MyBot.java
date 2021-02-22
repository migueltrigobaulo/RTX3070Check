import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

public class MyBot extends TelegramLongPollingBot {

    ArrayList<Long> userChatIds = new ArrayList<>();

    /**
     * Method for receiving messages.
     *
     * @param update Contains a message from the user.
     */
    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        if (message.equals("keepMeUpdated")) {
            if (!userChatIds.contains(update.getMessage().getChatId())) {
                userChatIds.add(update.getMessage().getChatId());
                sendMessage(update.getMessage().getChatId().toString(), "Ok");
            } else {
                sendMessage(update.getMessage().getChatId().toString(), "Already subscribed");
            }
        }
    }

    /**
     * Method for creating a message and sending it.
     *
     * @param chatId  chat id
     * @param message The String that you want to send as a message.
     */
    public synchronized void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns the bot's name, which was specified during registration.
     *
     * @return bot name
     */
    @Override
    public String getBotUsername() {
        return "BotName";
    }

    public synchronized void sendMessageToAll(String message) {
        for (Long userChatId : userChatIds) {
            sendMessage(userChatId.toString(), message);
        }
    }

    /**
     * This method returns the bot's token for communicating with the Telegram server
     *
     * @return the bot's token
     */
    @Override
    public String getBotToken() {
        return "token";
    }
}