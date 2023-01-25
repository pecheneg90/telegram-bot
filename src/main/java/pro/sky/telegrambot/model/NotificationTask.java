package pro.sky.telegrambot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = NotificationTask.TABLE_NAME)
@Setter
@Getter
@NoArgsConstructor
public class NotificationTask {
    public static final String TABLE_NAME = "notification_task";
    @Id
    @GeneratedValue
    private Integer key;
    @Column(name = "id_chat")
    private Long idChat;
    private String message;
    private LocalDateTime dateTime;
    @Column(name = "is_sent")
    private boolean isSent = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(key, that.key) && Objects.equals(idChat, that.idChat) && Objects.equals(message, that.message) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, idChat, message, dateTime);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "key=" + key +
                ", idChat=" + idChat +
                ", message='" + message + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}