package financeTracking.app.controller;

import financeTracking.app.dto.TransactionUpdateDTO;
import financeTracking.app.model.Transactions;
import financeTracking.app.service.TransactionService;
import financeTracking.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PutControllerTest {
    @InjectMocks
    private PutContoller putController;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    private TransactionUpdateDTO transactionUpdateDTO;
    private Transactions mockTransaction;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Sample data setup for the test
        transactionUpdateDTO = new TransactionUpdateDTO();
        transactionUpdateDTO.setAmount(150.0);

        mockTransaction = new Transactions();
        mockTransaction.setId("abc123");
        mockTransaction.setAmount(150.0);
    }

    @Test
    public void testUpdateTransaction_UserNotFound() {
        String userId = "12345";
        String transactionId = "abc123";

        // Mocking the userService to return false (user not found)
        when(userService.userExists(userId)).thenReturn(false);

        // Perform the PUT request and check the response
        ResponseEntity<?> response = putController.updateTransaction(userId, transactionId, transactionUpdateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testUpdateTransaction_Success() {
        String userId = "12345";
        String transactionId = "abc123";

        // Mocking the userService to return true (user exists)
        when(userService.userExists(userId)).thenReturn(true);
        when(transactionService.updateTransaction(eq(userId), eq(transactionId), any(TransactionUpdateDTO.class)))
                .thenReturn(mockTransaction);

        // Perform the PUT request and check the response
        ResponseEntity<?> response = putController.updateTransaction(userId, transactionId, transactionUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTransaction, response.getBody());
    }

    @Test
    public void testUpdateTransaction_Failure() {
        String userId = "12345";
        String transactionId = "abc123";

        // Mocking the userService to return true (user exists)
        when(userService.userExists(userId)).thenReturn(true);
        when(transactionService.updateTransaction(eq(userId), eq(transactionId), any(TransactionUpdateDTO.class)))
                .thenThrow(new RuntimeException("Transaction update failed"));

        // Perform the PUT request and check the response
        ResponseEntity<?> response = putController.updateTransaction(userId, transactionId, transactionUpdateDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Transaction update failed", response.getBody());
    }
}
