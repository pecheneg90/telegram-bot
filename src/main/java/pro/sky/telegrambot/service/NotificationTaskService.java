package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskService {
    private final Logger logger = LoggerFactory.getLogger(NotificationTaskService.class);
    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBot telegramBot;
    Pattern pattern = Pattern.compile
            ("(0[1-9]|[12]\\d|3[01]).(0?[1-9]|1[012]).((?:19|20)\\d\\d) (0\\d|1\\d|2[0-3]):([0-5]\\d) ([\\s\\S]*)");

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }

    public String getPattern() {
        return String.valueOf(pattern);
    }

    public SendMessage create(Update update) {
        logger.info("Вызван метод: " + this.getClass().getSimpleName() + " Создание уведомления/задачи");
        try {
            Matcher matcher = Pattern.compile(String.valueOf(pattern)).matcher(update.message().text());

            if (matcher.find()) {
                LocalDateTime nowDateTime = LocalDateTime.now();
                LocalDateTime notificationDateTime = LocalDateTime.parse(matcher.group(3)
                        + "-" + matcher.group(2)
                        + "-" + matcher.group(1)
                        + "T" + matcher.group(4)
                        + ":" + matcher.group(5)
                        + ":00");
                if (notificationDateTime.isBefore(nowDateTime) || notificationDateTime.isEqual(nowDateTime)) {
                    return new SendMessage(update.message().chat().id(), "Не актуальная дата и время");
                }
                NotificationTask notificationTask = new NotificationTask();
                notificationTask.setIdChat(update.message().chat().id());
                notificationTask.setMessage(matcher.group(6));
                notificationTask.setDateTime(notificationDateTime);

                if (
                        notificationTaskRepository.
                                existsByIdChatEqualsAndMessageEqualsIgnoreCaseAndDateTimeEqualsAndIsSentFalse
                                        (notificationTask.getIdChat(),
                                                notificationTask.getMessage(),
                                                notificationTask.getDateTime())
                ) {
                    return new SendMessage(update.message().chat().id(), "Уведомление уже есть");
                }

                notificationTaskRepository.save(notificationTask);

                return new SendMessage(update.message().chat().id(), "Сохранено");
            }
            return new SendMessage(update.message().chat().id(), "Некорректные данные");
        } catch (Exception exception) {
            return new SendMessage(update.message().chat().id(), "Не сохранено");
        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendingNotifications() {
        logger.info("Был вызван метод: " + this.getClass().getSimpleName() + " Отправка уведомления");

        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> list = notificationTaskRepository.findByDateTimeLessThanEqualAndIsSentFalse(date);

        for (NotificationTask notificationTask : list) {
            telegramBot.execute(new SendMessage(notificationTask.getIdChat(), notificationTask.getMessage()));
            notificationTask.setSent(true);
            notificationTaskRepository.save(notificationTask);
        }
    }
}