import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public abstract class BAcc implements TransactionOperations {
    public static final String ACTIVE = "ACTIVE";
    private static final int HASH_ITERATIONS = 120000;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final String accountNumber;
    private final String holderName;
    private final String accountType;
    private final LocalDateTime createdAt;
    private final byte[] pinSalt;
    private byte[] pinHash;
    private double balance;
    private String status;
    private int failedAttempts;
    private long lockedUntil;

    protected BAcc(String accountNumber, String holderName, String pin, double initialDeposit) {
        this(accountNumber, holderName, "ACCOUNT", initialDeposit, LocalDateTime.now(), pin, ACTIVE);
    }

    protected BAcc(String accountNumber, String holderName, String accountType, double balance,
                   LocalDateTime createdAt, String pin, String status) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.pinSalt = newSalt();
        this.pinHash = hashPin(pin, this.pinSalt);
        this.status = status == null ? ACTIVE : status;
    }

    protected BAcc(String accountNumber, String holderName, String accountType, double balance,
                   LocalDateTime createdAt, byte[] pinSalt, byte[] pinHash, String status) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.pinSalt = pinSalt == null ? newSalt() : pinSalt.clone();
        this.pinHash = pinHash == null ? hashPin("0000", this.pinSalt) : pinHash.clone();
        this.status = status == null ? ACTIVE : status;
    }

    private static byte[] newSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static byte[] hashPin(String pin, byte[] salt) {
        try {
            byte[] result = salt.clone();
            for (int i = 0; i < HASH_ITERATIONS; i++) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(result);
                digest.update(pin.getBytes(StandardCharsets.UTF_8));
                result = digest.digest();
            }
            return result;
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    public boolean verifyPin(String pin) throws InvalidPinException {
        if (isLocked()) throw new InvalidPinException("Account is temporarily locked. Try again later.");
        byte[] supplied = hashPin(pin, pinSalt);
        if (!MessageDigest.isEqual(supplied, pinHash)) {
            failedAttempts++;
            if (failedAttempts >= 3) {
                lockedUntil = System.currentTimeMillis() + 60000L;
                failedAttempts = 0;
                throw new InvalidPinException("Three incorrect attempts. Account locked for one minute.");
            }
            throw new InvalidPinException("Incorrect PIN. Attempt " + failedAttempts + " of 3.");
        }
        failedAttempts = 0;
        return true;
    }

    public boolean isLocked() { return System.currentTimeMillis() < lockedUntil; }

    public void changePin(String newPin) throws InvalidPinException {
        if (newPin == null || !newPin.matches("\\d{4}")) {
            throw new InvalidPinException("PIN must contain exactly four digits.");
        }
        pinHash = hashPin(newPin, pinSalt);
    }

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public String getAccountType() { return accountType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public byte[] getPinSalt() { return pinSalt.clone(); }
    public byte[] getPinHash() { return pinHash.clone(); }
    protected void setBalance(double balance) { this.balance = balance; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAtText() { return createdAt.format(DATE_FORMAT); }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        balance += amount;
    }

    protected void validateAmount(double amount) throws InvalidAmountException {
        if (!Double.isFinite(amount) || amount <= 0 || amount > 10000000) {
            throw new InvalidAmountException("Enter an amount greater than zero and below ₹1,00,00,000.");
        }
    }

    public abstract void withdraw(double amount) throws InvalidAmountException, InsufficientBalanceException;
    public abstract String getRulesSummary();

    public String toSafeString() {
        return accountNumber + " • " + holderName + " • " + accountType;
    }
}
