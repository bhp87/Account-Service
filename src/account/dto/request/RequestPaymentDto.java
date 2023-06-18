package account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class RequestPaymentDto {
    String employee;
    String period;
    long salary;

}