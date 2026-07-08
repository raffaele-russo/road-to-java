package bank;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 essentials: lifecycle annotations, assertions, exception testing,
 * parameterized tests, nested test classes for grouping. Run: mvn test
 */
class BankAccountTest {

    private BankAccount account;

    @BeforeEach                                    // runs before EVERY test — fresh fixture
    void setUp() {
        account = new BankAccount("Ada", 100.0);
    }

    @Test
    @DisplayName("deposit increases the balance")
    void depositIncreasesBalance() {
        account.deposit(50.0);
        assertEquals(150.0, account.balance(), 0.001);   // delta for double comparison
    }

    @Test
    void withdrawDecreasesBalance() {
        account.withdraw(30.0);
        assertEquals(70.0, account.balance());
    }

    @Test
    void withdrawMoreThanBalanceThrows() {
        // assertThrows: the idiomatic way to test exceptions (no try/catch needed)
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> account.withdraw(1000.0));
        assertTrue(ex.getMessage().contains("insufficient"));
    }

    @Test
    void negativeInitialBalanceThrows() {
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("Bob", -1));
    }

    @ParameterizedTest                             // runs once per value, same test logic
    @ValueSource(doubles = {-5, 0})
    void nonPositiveDepositThrows(double badAmount) {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(badAmount));
    }

    @Nested                                        // groups related tests, own lifecycle
    @DisplayName("when the account is empty")
    class WhenEmpty {
        @BeforeEach
        void makeEmpty() { account.withdraw(100.0); }

        @Test
        void balanceIsZero() {
            assertEquals(0.0, account.balance());
        }

        @Test
        void anyWithdrawalFails() {
            assertThrows(IllegalStateException.class, () -> account.withdraw(0.01));
        }
    }
}
