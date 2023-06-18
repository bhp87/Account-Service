package account.dto.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseErrorDto {
    Date date;
    int status;
    String error;
    String message;
    String path;

    public ResponseErrorDto(int statusCode, String status, String message, String path) {
        this.date = new Date();
        this.status = statusCode;
        this.error = status;
        this.message = message;
        this.path = path;
    }
}