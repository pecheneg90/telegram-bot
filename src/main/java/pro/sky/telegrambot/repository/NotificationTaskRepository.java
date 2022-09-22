package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    @Query("select (count(n) > 0) from NotificationTask n " +
            "where n.idChat = ?1 and upper(n.message) = upper(?2) and n.dateTime = ?3 and n.isSent = false")
    boolean existsByIdChatEqualsAndMessageEqualsIgnoreCaseAndDateTimeEqualsAndIsSentFalse(
            @NonNull long chatId, @NonNull String message, @NonNull LocalDateTime datetime
    );
    @Query("select n from NotificationTask n where n.dateTime <= ?1 and n.isSent = false")
    List<NotificationTask> findByDateTimeLessThanEqualAndIsSentFalse(@NonNull LocalDateTime date);
}