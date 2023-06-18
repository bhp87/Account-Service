package account.service;

import account.dto.request.RequestPaymentDto;
import account.dto.response.ResponseErrorDto;
import account.model.Payment;
import account.model.User;
import account.repo.PaymentRepository;
import account.repo.SecurityEventsRepository;
import account.repo.UserRepository;
import account.service.interfaces.IAccountantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static account.Utils.*;

@Service
public class AccountantServiceImpl implements IAccountantService {
    private final SimpleDateFormat inputFormatter = new SimpleDateFormat("MM-yyyy");
    PaymentRepository paymentRepository;
    UserRepository userRepository;
    SecurityEventsRepository securityEventsRepository;

    @Autowired
    public AccountantServiceImpl(PaymentRepository paymentRepository, UserRepository userRepository, SecurityEventsRepository securityEventsRepository) {
        this.securityEventsRepository = securityEventsRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }


    @Override
    public ResponseEntity<?> uploadPayrols(List<RequestPaymentDto> requestPaymentDtos) {
        System.out.println(requestPaymentDtos);
        List<Payment> paymentList = new ArrayList<>();
        Map<String, String> errors = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        for (RequestPaymentDto payment : requestPaymentDtos) {
            Optional<User> user = userRepository.findByEmailIgnoreCase(payment.getEmployee());
            if (user.isEmpty()) {
                errors.put("user", USER_NOT_EXISTS);
            }
            if (payment.getSalary() <= 0) {
                errors.put("salary", SALARY_MUST_BE_POSITIVE);
            }
            try {
                System.out.println("trying to convert the date");
                paymentList.add(convertDtoToEntity(user.get(), payment));
            } catch (ParseException e) {
                errors.put("date", WRONG_DATE_MESSAGE);
            }
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createNewResponseErrorDto(httpStatus.value(), BAD_REQUEST, String.valueOf(errors), PAYMENTS));
        }
        try {
            List<Payment> thePAyments = paymentRepository.saveAll(paymentList);
        } catch (Exception e) {
            errors.put("date", WRONG_DATE_FORMAT);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createNewResponseErrorDto(httpStatus.value(), BAD_REQUEST, String.valueOf(errors), PAYMENTS));
        }
        return ResponseEntity.ok(Map.of("status", "Added successfully!"));

    }

    private Payment convertDtoToEntity(User user, RequestPaymentDto payment) throws ParseException {
        Date date = convertStringToDate(payment.getPeriod());
        return new Payment(user, date, payment.getSalary());
    }

    private Date convertStringToDate(String period) throws ParseException {
        int month = Integer.parseInt(period.split("-")[0]);
        int year = Integer.parseInt(period.split("-")[1]);
        if (month > 12 || month < 1) {
            throw new ParseException(WRONG_DATE_FORMAT, 0);
        }
        return inputFormatter.parse(period);


    }


    private ResponseErrorDto createNewResponseErrorDto(int value, String badRequest, String message, String addPayments) {
        return new ResponseErrorDto(value, badRequest, message, addPayments);
    }


    @Override
    public ResponseEntity<?> updatePayment(RequestPaymentDto payment) {
        System.out.println(payment);
        Map<String, String> errors = new HashMap<>();
        Optional<User> user = userRepository.findByEmailIgnoreCase(payment.getEmployee());
        if (user.isEmpty()) {
            errors.put("user", USER_NOT_EXISTS);
        }
        try {
            Payment updatedPayment = convertDtoToEntity(user.get(), payment);
            Payment paymentToUpdate = paymentRepository.findByPeriodAndEmployee(updatedPayment.getPeriod(), updatedPayment.getEmployee());
            updatedPayment.setId(paymentToUpdate.getId());
            paymentRepository.save(updatedPayment);
        } catch (Exception e) {
            errors.put("date", WRONG_DATE_MESSAGE);
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    createNewResponseErrorDto(
                            HttpStatus.BAD_REQUEST.value(),
                            BAD_REQUEST,
                            String.valueOf(errors),
                            PAYMENTS));
        }

        return ResponseEntity.ok(Map.of("status", "Updated successfully!"));
    }
}