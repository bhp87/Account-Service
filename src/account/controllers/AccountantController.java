package account.controllers;

import account.dto.request.RequestPaymentDto;
import account.service.interfaces.IAccountantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acct/")
public class AccountantController {
    IAccountantService iAccountantService;

    @Autowired
    public AccountantController(IAccountantService iAccountantService) {
        this.iAccountantService = iAccountantService;
    }

    @PostMapping("payments")
    ResponseEntity<?> uploadPayrols(@RequestBody List<RequestPaymentDto> payments) {
        return iAccountantService.uploadPayrols(payments);
    }

    @PutMapping("payments")
    ResponseEntity<?> updatePayment(@RequestBody RequestPaymentDto payment) {
        return iAccountantService.updatePayment(payment);
    }


}