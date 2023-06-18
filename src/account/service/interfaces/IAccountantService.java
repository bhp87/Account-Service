package account.service.interfaces;

import account.dto.request.RequestPaymentDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAccountantService {

    ResponseEntity<?> uploadPayrols(List<RequestPaymentDto> payments);

    ResponseEntity<?> updatePayment(RequestPaymentDto payment);
}