package account.service;


import account.dto.ROLES;
import account.dto.request.RequestPasswordChangeDto;
import account.dto.request.RequestRegisterDto;
import account.dto.response.ResponseErrorDto;
import account.dto.response.ResponsePasswordChangeDto;
import account.dto.response.ResponseRegisterDto;
import account.model.SecurityEvent;
import account.repo.SecurityEventsRepository;
import account.repo.UserRepository;
import account.model.User;
import account.service.interfaces.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static account.Utils.*;
import static account.dto.ROLES.ANONYMOUS;
import static account.model.SecurityAction.CHANGE_PASSWORD;
import static account.model.SecurityAction.CREATE_USER;


@Service
public class UserServiceImpl implements IUserService {

    UserRepository repo;
    ModelMapper mapper;
    PasswordEncoder encoder;
    SecurityEventsRepository securityEventsRepository;

    @Autowired
    public UserServiceImpl(UserRepository repo, ModelMapper mapper, PasswordEncoder encoder, SecurityEventsRepository securityEventsRepository) {
        this.repo = repo;
        this.mapper = mapper;
        this.encoder = encoder;
        this.securityEventsRepository = securityEventsRepository;
    }


    @Override
    public ResponseEntity<?> register(RequestRegisterDto registerDto) {
        System.out.println(registerDto);
        registerDto.setRoles(repo.count() > 0 ? List.of("ROLE_" + ROLES.USER) : List.of("ROLE_" + ROLES.ADMINISTRATOR));
        Optional<User> optUser = repo.findByEmailIgnoreCase(registerDto.getEmail());
        System.out.println(optUser);
        if (passwordBreached(registerDto.getPassword())) {
            return ResponseEntity.badRequest().body(createResponseErrorDto(
                    HttpStatus.BAD_REQUEST.value(),
                    BAD_REQUEST,
                    BREACHED_PASSWORD_MESSAGE,
                    REGISTER_PATH)
            );
        }
        User user;
        if (optUser.isPresent()) {
            ResponseErrorDto errorDto = createResponseErrorDto(
                    HttpStatus.BAD_REQUEST.value(),
                    BAD_REQUEST,
                    USER_EXISTS,
                    REGISTER_PATH);
            return ResponseEntity.badRequest().body(errorDto);

        }

        registerDto.setPassword(encoder.encode(registerDto.getPassword()));
        user = repo.save(mapper.map(registerDto, User.class));
        securityEventsRepository.save(new SecurityEvent(CREATE_USER, ANONYMOUS.getRole(), user.getEmail(), REGISTER_PATH));
        System.out.println(user);
        return ResponseEntity.ok(mapper.map(user, ResponseRegisterDto.class));
    }

    private ResponseErrorDto createResponseErrorDto(int statusCode, String status, String message, String path) {
        return new ResponseErrorDto(
                statusCode,
                status,
                message,
                path);
    }


    private boolean passwordBreached(String password) {
        return Arrays.asList(BREACHED_PASSWORDS).contains(password);
    }


    @Override
    public ResponseEntity<?> changePassword(String email, RequestPasswordChangeDto newPassword) {

        User user = repo.findByEmailIgnoreCase(email).orElseThrow();


        int statusCode = HttpStatus.BAD_REQUEST.value();
        if (encoder.matches(newPassword.getPassword(), user.getPassword())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseErrorDto(statusCode, BAD_REQUEST, DIFFERENT_PASSWORD_MESSAGE, CHANGE_PASSWORD_PATH));
        }
        if (passwordBreached(newPassword.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseErrorDto(statusCode, BAD_REQUEST, BREACHED_PASSWORD_MESSAGE, CHANGE_PASSWORD_PATH));
        }
        String encodedNewPassword = encoder.encode(newPassword.getPassword());
        user.setPassword(encodedNewPassword);
        repo.save(user);
        securityEventsRepository.save(new SecurityEvent(CHANGE_PASSWORD, user.getEmail(), user.getEmail(), CHANGE_PASSWORD_PATH));

        return ResponseEntity.ok(new ResponsePasswordChangeDto(email));
    }
}