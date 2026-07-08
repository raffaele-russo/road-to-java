package notify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Mockito essentials: @Mock, stubbing with when/thenReturn, verify() with call
 * counts, ArgumentCaptor to inspect what was passed, and stubbing a failure path.
 * Run: mvn test
 */
@ExtendWith(MockitoExtension.class)               // wires up @Mock fields automatically
class NotificationServiceTest {

    @Mock
    EmailSender sender;                            // fake EmailSender — no real email sent

    @Test
    void notifyUser_sendsEmail_whenAddressValid() {
        when(sender.send(anyString(), anyString(), anyString())).thenReturn(true); // stub

        NotificationService service = new NotificationService(sender);
        boolean result = service.notifyUser("ada@example.com", "signup");

        assertTrue(result);
        verify(sender, times(1)).send(eq("ada@example.com"), anyString(), anyString()); // called once
    }

    @Test
    void notifyUser_capturesExactMessageSent() {
        when(sender.send(anyString(), anyString(), anyString())).thenReturn(true);
        NotificationService service = new NotificationService(sender);

        service.notifyUser("ada@example.com", "password-reset");

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender).send(anyString(), anyString(), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("password-reset")); // inspect the real argument
    }

    @Test
    void notifyUser_rejectsInvalidEmail_withoutCallingSender() {
        NotificationService service = new NotificationService(sender);

        assertThrows(IllegalArgumentException.class,
            () -> service.notifyUser("not-an-email", "signup"));

        verifyNoInteractions(sender);              // guard against accidental side effects
    }

    @Test
    void notifyUser_propagatesFalse_whenSendFails() {
        when(sender.send(anyString(), anyString(), anyString())).thenReturn(false); // simulate failure
        NotificationService service = new NotificationService(sender);

        assertFalse(service.notifyUser("ada@example.com", "signup"));
    }
}
