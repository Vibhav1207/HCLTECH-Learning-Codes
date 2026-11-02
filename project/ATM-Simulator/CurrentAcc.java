import java.time.LocalDateTime;

public class CurrentAcc extends BAcc {
    public static final double OVERDRAFT_LIMIT = 10000.0;

    public CurrentAcc(String accountNumber, String holderName, String pin, double initialDeposit) {
        super(accountNumber, holderName, "CURRENT", initialDeposit, LocalDateTime.now(), pin, ACTIVE);
    }

    public CurrentAcc(String accountNumber, String holderName, double balance, LocalDateTime createdAt,
                      byte[] salt, byte[] hash, String status) {
        super(accountNumber, holderName, "CURRENT", balance, createdAt, salt, hash, status);
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientBalanceException {
        validateAmount(amount);
        if (getBalance() - amount < -OVERDRAFT_LIMIT) {
            throw new InsufficientBalanceException("Current account overdraft limit is ₹10,000.");
        }
        setBalance(getBalance() - amount);
    }

    @Override
    public String getRulesSummary() { return "Overdraft facility up to ₹10,000"; }
}
