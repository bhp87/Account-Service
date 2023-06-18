package account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseSecurityEvent {
    Date date;
    String action;
    String subject;
    String object;
    String path;

}

