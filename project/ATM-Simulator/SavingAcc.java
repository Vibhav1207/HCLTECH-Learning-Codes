import java.time.LocalDateTime;

public class SavingAcc extends BAcc {
    public static final double MINIMUM_BALANCE = 500.0;
    public static final double INTEREST_RATE = 4.5;

    public SavingAcc(String accountNumber, String holderName, String pin, double initialDeposit) {
        super(accountNumber, holderName, "SAVINGS", initialDeposit, LocalDateTime.now(), pin, ACTIVE);
    }

    public SavingAcc(String accountNumber, String holderName, double balance, LocalDateTime createdAt,
                     byte[] salt, byte[] hash, String status) {
        super(accountNumber, holderName, "SAVINGS", balance, createdAt, salt, hash, status);
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientBalanceException {
        validateAmount(amount);
        if (getBalance() - amount < MINIMUM_BALANCE) {
            throw new InsufficientBalanceException("Savings accounts must retain a minimum balance of ₹500.");
        }
        setBalance(getBalance() - amount);
    }

    @Override
    public String getRulesSummary() { return "Minimum balance ₹500 • Interest 4.5% p.a."; }

    public double calculateAnnualInterest() { return getBalance() * INTEREST_RATE / 100.0; }
}
