package account.service;


import account.dto.request.RequestChangeUserAccessDto;
import account.dto.request.RequestRoleDto;
import account.dto.response.ResponseErrorDto;
import account.dto.response.ResponseRegisterDto;
import account.model.SecurityEvent;
import account.model.User;
import account.repo.SecurityEventsRepository;
import account.repo.UserRepository;
import account.service.interfaces.IAdminService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static account.dto.ROLES.*;
import static account.model.SecurityAction.*;

@Service
public class AdminServiceImpl implements IAdminService {
    UserRepository userRepository;
    ModelMapper mapper;
    SecurityEventsRepository securityEventsRepository;
    StringBuilder logger_object = new StringBuilder();

    @Autowired
    public AdminServiceImpl(UserRepository userRepository, ModelMapper mapper, SecurityEventsRepository securityEventsRepository) {
        this.securityEventsRepository = securityEventsRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<?> setUserRole(RequestRoleDto requestRoleDto, Principal principal) {
        logger_object.delete(0, logger_object.length());
        Optional<User> user = userRepository.findByEmailIgnoreCase(requestRoleDto.getUser());
        /**
         * This is a list of existing roles in the application.
         */
        List<String> existingRoles = List.of(USER.toString(), ADMINISTRATOR.toString(), ACCOUNTANT.toString(), AUDITOR.toString());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResponseErrorDto(404, "Not Found", "User not found!", "/api/admin/user/role"));
        }
        List<String> roles = user.get().getRoles();
        if (!existingRoles.contains(requestRoleDto.getRole())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResponseErrorDto(404, "Not Found", "Role not found!", "/api/admin/user/role"));
        }
        if ((requestRoleDto.getRole().contains(ADMINISTRATOR.toString()) && requestRoleDto.getOperation().equals("GRANT")) || roles.contains("ROLE_" + ADMINISTRATOR) && requestRoleDto.getOperation().equals("GRANT")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseErrorDto(400, "Bad Request", "The user cannot combine administrative and business roles!", "/api/admin/user/role"));
        }
        if (requestRoleDto.getOperation().equals("REMOVE") && requestRoleDto.getRole().equals("ADMINISTRATOR")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseErrorDto(400, "Bad Request", "Can't remove " + ADMINISTRATOR + " role!", "/api/admin/user/role"));
        }
        if (!user.get().getRoles().contains("ROLE_" + requestRoleDto.getRole()) && requestRoleDto.getOperation().equals("REMOVE")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseErrorDto(400, "Bad Request", "The user does not have a role!", "/api/admin/user/role"));
        }
        if (user.get().getRoles().size() == 1 && requestRoleDto.getOperation().equals("REMOVE")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseErrorDto(400, "Bad Request", "The user must have at least one role!", "/api/admin/user/role"));
        }


        User updatedUser;
        if (requestRoleDto.getOperation().equals("REMOVE")) {
            user.get().getRoles().remove("ROLE_" + requestRoleDto.getRole());
            updatedUser = userRepository.save(user.get());
            logger_object.append("Remove role ").append(requestRoleDto.getRole()).append(" from ").append(user.get().getEmail());
            securityEventsRepository.save(new SecurityEvent(REMOVE_ROLE, principal.getName(), logger_object.toString(), "/api/admin/user/role"));
            return ResponseEntity.ok(mapper.map(updatedUser, ResponseRegisterDto.class));

        }

        roles.add("ROLE_" + requestRoleDto.getRole());
        user.get().setRoles(roles);
        roles.sort(String::compareTo);
        updatedUser = userRepository.save(user.get());
        logger_object.append("Grant role ").append(requestRoleDto.getRole()).append(" to ").append(user.get().getEmail());
        securityEventsRepository.save(new SecurityEvent(GRANT_ROLE, principal.getName(), logger_object.toString(), "/api/admin/user/role"));
        return ResponseEntity.ok(mapper.map(updatedUser, ResponseRegisterDto.class));
    }

    private ResponseErrorDto createResponseErrorDto(int statusCode, String status, String message, String path) {
        return new ResponseErrorDto(
                statusCode,
                status,
                message,
                path);
    }


    @Override
    public ResponseEntity<?> deleteUser(String email, Principal principal) {
        logger_object.delete(0, logger_object.length());
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResponseErrorDto(404, "Not Found", "User not found!", "/api/admin/user/" + email));
        }
        if (user.get().getRoles().contains("ROLE_ADMINISTRATOR")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseErrorDto(400, "Bad Request", "Can't remove ADMINISTRATOR role!", "/api/admin/user/" + email));
        }
        userRepository.delete(user.get());

        logger_object.append("Delete user ").append(email);
        securityEventsRepository.save(new SecurityEvent(DELETE_USER, principal.getName(), logger_object.toString(), "/api/admin/user/role"));

        return ResponseEntity.ok(
                "{\n \"user\": \"" + email +
                        "\",\n \"status\": \"Deleted successfully!\"\n}");
    }


    @Override
    public ResponseEntity<?> getAllUsersInfo(Principal principal) {
        Optional<List<User>> users = userRepository.findAllByOrderById();
        ArrayList<ResponseRegisterDto> responseRegisterDtos = users.map(userList -> (ArrayList<ResponseRegisterDto>) userList.stream().map(user -> mapper.map(user, ResponseRegisterDto.class)).collect(Collectors.toList())).orElseGet(ArrayList::new);
        return ResponseEntity.ok(responseRegisterDtos);
    }

    @Override
    public ResponseEntity<?> changeUserAccess(RequestChangeUserAccessDto requestChangeUserAccessDto, String email) {
        logger_object.delete(0, logger_object.length());
        logger_object.append(email);
        String userEmail = requestChangeUserAccessDto.getUser().toLowerCase();
        Optional<User> optUser = userRepository.findByEmailIgnoreCase(requestChangeUserAccessDto.getUser());
        String operation = requestChangeUserAccessDto.getOperation();
        if (null == userEmail || null == operation || optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseErrorDto(400, "Bad Request", "User not found!", "api/admin/optUser/access"));
        }
        User user = optUser.get();
        if (user.getRoles().contains("ROLE_ADMINISTRATOR")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseErrorDto(400, "Bad Request", "Can't lock the ADMINISTRATOR!", "/api/admin/user/access"));
        }
        if (operation.equals("LOCK")) {
            user.setAccountNonLocked(false);
            securityEventsRepository.save(new SecurityEvent(LOCK_USER, logger_object.toString(), "Lock user " + userEmail, "/api/admin/user/access"));
        } else {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            securityEventsRepository.save(new SecurityEvent(UNLOCK_USER, logger_object.toString(), "Unlock user " + userEmail, "/api/admin/user/access"));
        }
        userRepository.save(user);
        String lock = operation.equals("LOCK") ? "locked" : "unlocked";
        return ResponseEntity.ok("{\n" +
                "    \"status\": \"User " + userEmail + " " + lock + "!\"\n" +
                "}");
    }
}
