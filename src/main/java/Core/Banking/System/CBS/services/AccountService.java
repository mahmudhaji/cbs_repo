package Core.Banking.System.CBS.services;

import Core.Banking.System.CBS.model.Account;
import Core.Banking.System.CBS.model.Customer;
import Core.Banking.System.CBS.model.Transaction;
import Core.Banking.System.CBS.repository.AccountRepository;
import Core.Banking.System.CBS.repository.CustomerRepository;
import Core.Banking.System.CBS.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private static final double TRANSACTION_CHARGE = 2.50; // transaction charges

    @Transactional
    public Account createAccount(Long customerId, Account account) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        account.setCustomer(customer);
        account.setAccountNumber(generateAccountNumber());
        return accountRepository.save(account);
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Transactional
    public Transaction processTransaction(Long sourceAccountId, Long destinationAccountId, double amount, String type) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same");
        }

        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        Account destinationAccount = accountRepository.findById(destinationAccountId)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        double charge = calculateCharge(type, amount);
        if (type.equalsIgnoreCase("DEBIT") && sourceAccount.getBalance() < (amount + charge)) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setType(type.toUpperCase());
        transaction.setAmount(amount);
        transaction.setCharge(charge);

        if (type.equalsIgnoreCase("DEBIT")) {
            sourceAccount.setBalance(sourceAccount.getBalance() - amount - charge);
            destinationAccount.setBalance(destinationAccount.getBalance() + amount);
        } else if (type.equalsIgnoreCase("CREDIT")) {
            sourceAccount.setBalance(sourceAccount.getBalance() + amount - charge);
            destinationAccount.setBalance(destinationAccount.getBalance() - amount);
        } else {
            throw new IllegalArgumentException("Invalid transaction type");
        }

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        return transactionRepository.save(transaction);
    }

    public Page<Transaction> getTransactionHistory(Long accountId, Pageable pageable) {
        return transactionRepository.findBySourceAccountIdOrDestinationAccountId(accountId, accountId, pageable);
    }

    private double calculateCharge(String type, double amount) {
        //  Flat charge for debit transactions, no charge for credits
        return type.equalsIgnoreCase("DEBIT") ? TRANSACTION_CHARGE : 0.0;
    }

    private String generateAccountNumber() {
        return "ACCT-" + System.currentTimeMillis();
    }
}