package account.service.util_services;

import account.dto.request.RequestChangeUserAccessDto;
import account.model.SecurityAction;
import account.model.User;
import account.repo.UserRepository;
import account.service.AuditorServiceImpl;
import account.service.interfaces.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static account.model.SecurityAction.LOCK_USER;
import static account.model.SecurityAction.LOGIN_FAILED;

@Service
public class LoginAttemptService {
    public static final int MAX_FAILED_ATTEMPTS = 5;
    private final UserRepository userRepository;
    private final AuditorServiceImpl auditorService;
    IAdminService adminService;

    @Autowired
    public LoginAttemptService(UserRepository userRepository, AuditorServiceImpl auditorService, IAdminService adminService) {
        this.userRepository = userRepository;
        this.auditorService = auditorService;
        this.adminService = adminService;
    }

    public void loginSuccess(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(User::new);
        user.setFailedAttempt(0);
        userRepository.save(user);
    }

    public void loginFailure(String email, String uri) {
        auditorService.addEvent(LOGIN_FAILED, email, uri, uri);
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        if (userOptional.isEmpty()) {
            return;
        }
        User user = userOptional.get();
        if (user.containsRole("ROLE_ADMINISTRATOR")) {
            return;
        }

        user.setFailedAttempt(user.getFailedAttempt() + 1);

        if (user.getFailedAttempt() >= MAX_FAILED_ATTEMPTS) {
            auditorService.addEvent(SecurityAction.BRUTE_FORCE, email, uri, uri);
            adminService.changeUserAccess(new RequestChangeUserAccessDto(email, "LOCK"), email);
            user.setAccountNonLocked(false);
        }
        userRepository.save(user);

    }
}
