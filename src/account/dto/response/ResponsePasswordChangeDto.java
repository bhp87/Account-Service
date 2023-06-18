package account.dto.response;

import lombok.*;

@Data
@AllArgsConstructor

public class ResponsePasswordChangeDto {
    @NonNull
    String email;
    String status = "The password has been updated successfully";

    public ResponsePasswordChangeDto(String email) {
        this.email = email;
    }

}