package account.dto;

import lombok.Getter;

@Getter
public enum ROLES {
    ANONYMOUS("Anonymous"), USER("User"), ADMINISTRATOR("Administrator"), AUDITOR("Auditor"), ACCOUNTANT("Accountant");
    private String role;

    ROLES(String role) {
        this.role = role;
    }
}
