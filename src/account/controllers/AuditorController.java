package account.controllers;

import account.service.interfaces.IAuditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security/")
public class AuditorController {
    IAuditorService auditorService;

    @Autowired
    public AuditorController(IAuditorService auditorService) {
        this.auditorService = auditorService;
    }

    @GetMapping("events/")
    ResponseEntity<?> getSecurityEvents() {
        return auditorService.getSecurityEvents();
    }
}
