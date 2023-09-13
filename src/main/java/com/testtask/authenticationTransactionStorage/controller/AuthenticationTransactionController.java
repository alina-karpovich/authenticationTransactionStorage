package com.testtask.authenticationTransactionStorage.controller;

import com.testtask.authenticationTransactionStorage.controller.exception.TransactionIdMismatchException;
import com.testtask.authenticationTransactionStorage.controller.exception.TransactionNotFoundException;
import com.testtask.authenticationTransactionStorage.persistence.model.AuthenticationTransaction;
import com.testtask.authenticationTransactionStorage.persistence.repo.AuthenticationTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class AuthenticationTransactionController {

    @Autowired
    private AuthenticationTransactionRepo transactionRepo;

    @GetMapping
    public Iterable<AuthenticationTransaction> findAll() {
        return transactionRepo.findAll();
    }

    @GetMapping("/type/{transactionType}")
    public List<AuthenticationTransaction> findByType(@PathVariable String transactionType) {
        return transactionRepo.findByType(transactionType);
    }

    @GetMapping("/actor/{actor}")
    public List<AuthenticationTransaction> findByActor(@PathVariable String actor) {
        return transactionRepo.findByActor(actor);
    }

    @GetMapping("/{id}")
    public AuthenticationTransaction findById(@PathVariable long id) {
        return transactionRepo.findById(id)
          .orElseThrow(TransactionNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationTransaction create(@RequestBody AuthenticationTransaction transaction) {
        return transactionRepo.save(transaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        transactionRepo.findById(id)
          .orElseThrow(TransactionNotFoundException::new);
        transactionRepo.deleteById(id);
    }

    @PutMapping("/{id}")
    public AuthenticationTransaction update(@RequestBody AuthenticationTransaction transaction, @PathVariable long id) {
        if (transaction.getId() != id) {
            throw new TransactionIdMismatchException();
        }
        transactionRepo.findById(id)
          .orElseThrow(TransactionNotFoundException::new);
        return transactionRepo.save(transaction);
    }
}
