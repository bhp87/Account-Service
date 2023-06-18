package account.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPasswordChangeDto {
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @NotBlank
    @JsonProperty(value = "new_password")
    String password;
}