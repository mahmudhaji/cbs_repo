package Core.Banking.System.CBS.controller;

import Core.Banking.System.CBS.DTOs.AccountDTO;
import Core.Banking.System.CBS.DTOs.TransactionDTO;
import Core.Banking.System.CBS.model.Account;
import Core.Banking.System.CBS.model.Transaction;
import Core.Banking.System.CBS.services.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/{customerId}")
    public ResponseEntity<AccountDTO> createAccount(@PathVariable Long customerId, @Valid @RequestBody AccountDTO dto) {
        Account account = modelMapper.map(dto, Account.class);
        Account saved = accountService.createAccount(customerId, account);
        return ResponseEntity.ok(modelMapper.map(saved, AccountDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        return ResponseEntity.ok(modelMapper.map(account, AccountDTO.class));
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionDTO> processTransaction(
            @RequestParam Long sourceAccountId,
            @RequestParam Long destinationAccountId,
            @RequestParam @Positive double amount,
            @RequestParam String type) {
        Transaction transaction = accountService.processTransaction(sourceAccountId, destinationAccountId, amount, type);
        return ResponseEntity.ok(modelMapper.map(transaction, TransactionDTO.class));
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(
            @PathVariable Long accountId,
            Pageable pageable) {
        Page<Transaction> transactions = accountService.getTransactionHistory(accountId, pageable);

        List<TransactionDTO> transactionList = transactions
                .map(t -> modelMapper.map(t, TransactionDTO.class))
                .getContent();  // ✅ Only take the content

        return ResponseEntity.ok(transactionList);
    }

}