package Core.Banking.System.CBS.DTOs;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private String type;
    private double amount;
    private double charge;
    private LocalDateTime timestamp;
}
