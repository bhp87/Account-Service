package account.service.interfaces;

import account.dto.request.RequestChangeUserAccessDto;
import account.dto.request.RequestRoleDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface IAdminService {
    ResponseEntity<?> setUserRole(RequestRoleDto requestRoleDto, Principal principal);

    ResponseEntity<?> deleteUser(String email, Principal principal);

    ResponseEntity<?> getAllUsersInfo(Principal principal);

    ResponseEntity<?> changeUserAccess(RequestChangeUserAccessDto requestChangeUserAccessDto, String email);
}
