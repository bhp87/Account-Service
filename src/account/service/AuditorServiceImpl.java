package account.service;

import account.model.SecurityAction;
import account.model.SecurityEvent;
import account.repo.SecurityEventsRepository;
import account.service.interfaces.IAuditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditorServiceImpl implements IAuditorService {
    SecurityEventsRepository securityEventsRepository;

    @Autowired
    public AuditorServiceImpl(SecurityEventsRepository securityEventsRepository) {
        this.securityEventsRepository = securityEventsRepository;
    }

    public void addEvent(SecurityAction action, String subject, String object, String path) {
        securityEventsRepository.save(new SecurityEvent(action, subject, object, path));
    }

    @Override
    public ResponseEntity<?> getSecurityEvents() {

        List<SecurityEvent> events = securityEventsRepository.findAll();
        System.out.println("-------------------------------------------------------------------");
        System.out.println(events);
        System.out.println("-------------------------------------------------------------------");
        return ResponseEntity.ok(events);
    }
}
