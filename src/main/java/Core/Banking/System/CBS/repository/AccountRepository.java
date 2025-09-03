package Core.Banking.System.CBS.repository;

import Core.Banking.System.CBS.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
