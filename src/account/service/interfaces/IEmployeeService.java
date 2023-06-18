package account.service.interfaces;

import account.dto.response.ResponseRegisterDto;
import org.springframework.http.ResponseEntity;

public interface IEmployeeService {
    ResponseEntity<?> getPayment(String period, String principal);
}