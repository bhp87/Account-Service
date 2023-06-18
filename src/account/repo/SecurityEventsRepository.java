package account.repo;

import account.model.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SecurityEventsRepository extends JpaRepository<SecurityEvent, Long> {
}
