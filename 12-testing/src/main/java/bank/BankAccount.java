package bank;

public class BankAccount {
    private final String owner;
    private double balance;

    public BankAccount(String owner, double initialBalance) {
        if (initialBalance < 0) throw new IllegalArgumentException("initial balance cannot be negative");
        this.owner = owner;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("deposit must be positive");
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("withdrawal must be positive");
        if (amount > balance) throw new IllegalStateException("insufficient funds");
        balance -= amount;
    }

    public double balance() { return balance; }
    public String owner() { return owner; }
}
