package Core.Banking.System.CBS.DTOs;

import lombok.Data;

@Data
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private double balance;
    private Long customerId;
}
