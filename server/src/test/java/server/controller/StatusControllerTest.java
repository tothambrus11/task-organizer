package server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.controllers.StatusController;

@ExtendWith(MockitoExtension.class)
public class StatusControllerTest {

    @InjectMocks
    private StatusController statusController;

    @Test
    public void testStatus() {
        ResponseEntity<String> response = statusController.status();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Server is up and running!", response.getBody());
    }
}
