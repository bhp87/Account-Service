package account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date date;
    @Enumerated(EnumType.STRING)
    private SecurityAction action;
    private String subject;
    private String object;
    private String path;

    public SecurityEvent(SecurityAction action, String subject, String object, String path) {
        this.date = new Date();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }
}
