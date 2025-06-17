package financeTracking.app.controller;

import financeTracking.app.model.User;
import financeTracking.app.payload.request.LoginRequest;
import financeTracking.app.payload.response.JwtResponse;
import financeTracking.app.security.jwt.JwtUtils;
import financeTracking.app.security.services.UserDetailsImpl;
import financeTracking.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    private LoginRequest loginRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Initialize the login request object with valid credentials
        loginRequest = new LoginRequest();
        loginRequest.setUsername("Vihanga Upamal");
        loginRequest.setPassword("123456");
    }

    @Test
    public void testAuthenticateUser_Success() {
        // Mock the authentication process for valid credentials
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Mock the user details
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("Vihanga");
        when(userDetails.getEmail()).thenReturn("vihanga@gmail.com");
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());

        // Mock the JWT token generation
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mockJwtToken");

        // Call the authenticateUser method
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("mockJwtToken", jwtResponse.getToken());
        assertEquals("Vihanga", jwtResponse.getUsername());
        assertEquals("vihanga@gmail.com", jwtResponse.getEmail());
        assertTrue(jwtResponse.getRoles().contains("ROLE_USER"));
    }

    @Test
    public void testAuthenticateUser_Failure_InvalidCredentials() {
        // Mock the authentication manager to throw an exception
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Invalid credentials"));

        // Call the authenticateUser method
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assertions
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue()); // Assuming that failure returns a 400 status
        assertEquals("Error: Invalid credentials", response.getBody());
    }
}
