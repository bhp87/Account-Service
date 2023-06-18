package account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"employee", "period" })})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee", referencedColumnName = "id")
    private User employee;

    @Column(name = "period")
    private Date period;

    @Column(name = "salary")
    private Long salary;

    public Payment(User user, Date date, long salary) {
        this.employee = user;
        this.period = date;
        this.salary = salary;
    }
}