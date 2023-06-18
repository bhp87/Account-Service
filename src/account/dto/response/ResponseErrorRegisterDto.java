package account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseErrorRegisterDto {
    long id;
    String name;
    String lastname;
    String email;
    //  String status;

}