package account.service.interfaces;

import org.springframework.http.ResponseEntity;

public interface IAuditorService {
    ResponseEntity<?> getSecurityEvents();
}
