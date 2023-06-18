package account.service;

import account.dto.response.ResponseErrorDto;
import account.dto.response.ResponsePayrollsDto;
import account.model.Payment;
import account.model.User;
import account.repo.PaymentRepository;
import account.repo.SecurityEventsRepository;
import account.repo.UserRepository;
import account.service.interfaces.IEmployeeService;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static account.Utils.*;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    private final SimpleDateFormat inputFormatter = new SimpleDateFormat("MM-yyyy");
    private final SimpleDateFormat outputFormatter = new SimpleDateFormat("MMMM-yyyy");
    UserRepository repo;
    ModelMapper mapper;
    PaymentRepository paymentRepository;
    SecurityEventsRepository securityEventsRepository;

    @Autowired
    public EmployeeServiceImpl(UserRepository repo, ModelMapper mapper, PaymentRepository paymentRepository, SecurityEventsRepository securityEventsRepository) {
        this.repo = repo;
        this.mapper = mapper;
        this.paymentRepository = paymentRepository;
        this.securityEventsRepository = securityEventsRepository;

    }

    @Override
    public ResponseEntity<?> getPayment(String period, String email) {
        if (!Strings.isNotBlank(email)) {
            return ResponseEntity.badRequest().body("This api only for authenticated user");
        }
        Optional<User> user = repo.findByEmailIgnoreCase(email);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        List<Payment> paymentList;
        if (Strings.isBlank(period)) {
            paymentList = paymentRepository.findAllByEmployeeOrderByPeriodDesc(user.get());
        } else {
            Date datePeriod;
            try {
                datePeriod = convertPeriodToDate(period);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(createNewResponseErrorDto(400, BAD_REQUEST, WRONG_DATE_FORMAT, GET_PAYMENT_PATH));
            }
            paymentList = List.of(paymentRepository.findByPeriodAndEmployee(datePeriod, user.get()));
        }
        if (paymentList.size() == 1) {
            return ResponseEntity.ok(convertToResponsePayrollDto(paymentList, user.get()).get(0));
        }
        List<ResponsePayrollsDto> responsePayrollsDtos = convertToResponsePayrollDto(paymentList, user.get());

        return ResponseEntity.ok(responsePayrollsDtos);
    }

    private List<ResponsePayrollsDto> convertToResponsePayrollDto(List<Payment> paymentList, User user) {
        List<ResponsePayrollsDto> result = new ArrayList<>();
        for (Payment payment : paymentList) {
            long salary = payment.getSalary();
            String salaryFormated = String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
            result.add(new ResponsePayrollsDto(user.getName(), user.getLastname(), outputFormatter.format(payment.getPeriod()), salaryFormated));
        }
        return result;
    }

    private Date convertPeriodToDate(String period) throws ParseException {
        int month = Integer.parseInt(period.substring(0, 2));
        int year = Integer.parseInt(period.substring(3));
        if (month < 1 || month > 12 || year < 1900 || year > 2100) {
            throw new ParseException("Incorrect period", 0);
        }
        return inputFormatter.parse(period);
    }

    private ResponseErrorDto createNewResponseErrorDto(int value, String badRequest, String message, String addPayments) {
        return new ResponseErrorDto(value, badRequest, message, addPayments);
    }
}