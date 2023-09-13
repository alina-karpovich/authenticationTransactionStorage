package com.testtask.authenticationTransactionStorage;

import com.testtask.authenticationTransactionStorage.persistence.model.AuthenticationTransaction;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationTransactionStorageLiveTest {

    private static final String API_ROOT = "http://localhost:8081/api/transactions";

    private enum TransactionType {
        AUTHENTICATE, AUTHORIZE, PAYMENT, DEFERRED
    }

    @Test
    public void getAllTransactions() {
        final Response response = RestAssured.get(API_ROOT);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

    @Test
    public void findTransactionByActor() {
        final AuthenticationTransaction transaction = createTestTransaction(TransactionType.AUTHENTICATE, "find_by_actor");
        postTransactionByUri(transaction);

        final Response response = RestAssured.get(API_ROOT + "/actor/" + transaction.getActor());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertTrue(response.as(List.class).size() > 0);
    }

    @Test
    public void findTransactionByType() {
        final AuthenticationTransaction transaction = createTestTransaction(TransactionType.AUTHENTICATE, "find_by_type");
        postTransactionByUri(transaction);

        final Response response = RestAssured.get(API_ROOT + "/type/" + transaction.getType());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertTrue(response.as(List.class).size() > 0);
    }

    @Test
    public void fetchById() {
        final AuthenticationTransaction transaction = createTestTransaction(TransactionType.AUTHENTICATE, "find_by_id");
        final String location = postTransactionByUri(transaction);

        final Response response = RestAssured.get(location);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(transaction.getType(), response.jsonPath().get("type"));
        assertEquals(transaction.getActor(), response.jsonPath().get("actor"));
    }

    @Test
    public void fetchNotExistingById() {
        final Response response = RestAssured.get(API_ROOT + "/" + Integer.MAX_VALUE);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    public void createNewTransaction() {
        final AuthenticationTransaction transaction = createTestTransaction(TransactionType.DEFERRED, "create_new", 3);

        final Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(transaction)
            .post(API_ROOT);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
    }

    @Test
    public void trySaveIncorrectTransaction() {
        final AuthenticationTransaction transaction = createTestTransaction(null, null);

        final Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(transaction)
            .post(API_ROOT);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    public void updateCreatedTransaction() {
        final AuthenticationTransaction transaction = createTestTransaction(TransactionType.PAYMENT, "update_transaction");
        final String location = postTransactionByUri(transaction);

        transaction.setId(Long.parseLong(location.split("api/transactions/")[1]));
        transaction.setActor("updated_actor");
        Response updateResponse = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(transaction)
            .put(location);
        assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode());

        Response fetchResponse = RestAssured.get(location);
        assertEquals(HttpStatus.OK.value(), fetchResponse.getStatusCode());
        assertEquals("updated_actor", fetchResponse.jsonPath().get("actor"));

    }

    @Test
    public void deleteCreatedTransaction() {
        final AuthenticationTransaction transaction = createTestTransaction(TransactionType.AUTHORIZE, "delete_transaction");
        final String location = postTransactionByUri(transaction);

        Response response = RestAssured.delete(location);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        response = RestAssured.get(location);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    // ===============================

    private AuthenticationTransaction createTestTransaction(TransactionType type, String actor) {
        return  createTestTransaction(type, actor, 0);
    }

    private AuthenticationTransaction createTestTransaction(TransactionType type, String actor, int dataParamCount) {
        final AuthenticationTransaction transaction = new AuthenticationTransaction();
        transaction.setTimestamp(new Date(System.currentTimeMillis()));
        transaction.setType(type != null ? type.name() : null);
        transaction.setActor(actor);
        Map<String, String> transactionData = new HashMap<>();
        for (int i = 0; i < dataParamCount; i++) {
            transactionData.put(randomAlphabetic(5), randomAlphabetic(10));
        }
        transaction.setTransactionData(transactionData);
        return transaction;
    }

    private String postTransactionByUri(AuthenticationTransaction transaction) {
        final Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(transaction)
            .post(API_ROOT);
        return API_ROOT + "/" + response.jsonPath().get("id");
    }

}