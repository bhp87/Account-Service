package account.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonRootName("response")
public class ResponseRegisterDto {
    long id;
    String name;
    String lastname;
    String email;
    List<String> roles;

    public ResponseRegisterDto(String name, String lastname, String email, List<String> roles) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
    }
}