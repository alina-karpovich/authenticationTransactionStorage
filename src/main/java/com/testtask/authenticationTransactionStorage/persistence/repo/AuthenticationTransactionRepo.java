package com.testtask.authenticationTransactionStorage.persistence.repo;

import com.testtask.authenticationTransactionStorage.persistence.model.AuthenticationTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthenticationTransactionRepo extends CrudRepository<AuthenticationTransaction, Long> {
    List<AuthenticationTransaction> findByType(String type);

    List<AuthenticationTransaction> findByActor(String actor);
}
