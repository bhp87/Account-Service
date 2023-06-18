package account.service.interfaces;


import account.dto.request.RequestPasswordChangeDto;
import account.dto.request.RequestRegisterDto;
import org.springframework.http.ResponseEntity;

public interface IUserService {

    ResponseEntity<?> register(RequestRegisterDto registerDto);



    ResponseEntity<?> changePassword(String email, RequestPasswordChangeDto newPassword);
}