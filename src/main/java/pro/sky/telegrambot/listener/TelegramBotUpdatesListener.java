package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final NotificationTaskService notificationTaskService;
    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService, TelegramBot telegramBot) {
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String helloText = "Привет! Бот может напомнить о событии! Напиши текст следующего содержания: " +
                    "ДД.ММ.ГГГГ ЧЧ:ММ Текст";
            Long chatId = update.message().chat().id();
            String messageText = update.message().text();
            if (update.message().text() != null) {
                if (messageText.equals("/start")) {
                    SendMessage message = new SendMessage(chatId, helloText);
                    telegramBot.execute(message);
                } else if (Pattern.matches(notificationTaskService.getPattern(), update.message().text())) {
                    telegramBot.execute(notificationTaskService.create(update));
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}