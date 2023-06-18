package account.repo;

import account.model.Payment;
import account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface PaymentRepository extends JpaRepository<Payment, Long> {


    List<Payment> findAllByEmployeeOrderByPeriodDesc(User user);

    Payment findByPeriodAndEmployee(Date period, User employee);
}