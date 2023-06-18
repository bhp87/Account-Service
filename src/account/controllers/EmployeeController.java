package account.controllers;


import account.service.interfaces.IEmployeeService;
import io.micrometer.core.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/empl/")
public class EmployeeController {
    IEmployeeService employeeService;

    @Autowired
    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("payment")
    ResponseEntity<?> getPayment(@RequestParam @Nullable String period, Principal principal) {
        return employeeService.getPayment(period, principal.getName());
    }
}