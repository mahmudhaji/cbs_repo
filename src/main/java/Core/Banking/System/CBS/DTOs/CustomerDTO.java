package Core.Banking.System.CBS.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDTO {
    private Long id;
    private String firstName;
    private String email;
    private String phone;
    private List<AccountDTO> accounts;
}
