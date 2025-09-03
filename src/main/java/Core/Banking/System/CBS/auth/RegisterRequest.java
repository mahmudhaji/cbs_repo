package Core.Banking.System.CBS.auth;

import Core.Banking.System.CBS.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String phone;
    private String email;
    private String password;
    private Role role;
}

