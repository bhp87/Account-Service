package account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestRegisterDto {
    @NotBlank
    String name;
    @NotBlank
    String lastname;


    @Pattern(regexp = "^\\w+@acme\\.com$", message = "Invalid email format or domain")
    @NotBlank
    @Length(min = 1)
    String email;
    @NotBlank
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    String password;

    List<String> roles;


}