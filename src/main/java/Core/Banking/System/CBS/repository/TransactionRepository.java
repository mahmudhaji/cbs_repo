package Core.Banking.System.CBS.repository;

import Core.Banking.System.CBS.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @EntityGraph(attributePaths = {"sourceAccount", "destinationAccount"})
    List<Transaction> findBySourceAccountIdOrDestinationAccountId(Long sourceAccountId, Long destinationAccountId);

    @EntityGraph(attributePaths = {"sourceAccount", "destinationAccount"})
    Page<Transaction> findBySourceAccountIdOrDestinationAccountId(Long sourceAccountId, Long destinationAccountId, Pageable pageable);
}
