package account.controllers;

import account.dto.request.RequestPasswordChangeDto;
import account.dto.request.RequestRegisterDto;
import account.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth/")
public class UserController {

    IUserService accountService;

    @Autowired
    public UserController(IUserService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("signup")
    ResponseEntity<?> register(@Valid @RequestBody RequestRegisterDto registerDto) {
        return accountService.register(registerDto);
    }

    @PostMapping("changepass")
    ResponseEntity<?> changePassword(Principal principal, @Valid @RequestBody RequestPasswordChangeDto newPassword) {
        return accountService.changePassword(principal.getName(), newPassword);
    }
}