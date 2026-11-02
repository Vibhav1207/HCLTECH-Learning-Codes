import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private final String id;
    private final String accountNumber;
    private final String type;
    private final double amount;
    private final String description;
    private final LocalDateTime dateTime;
    private final String status;

    public Transaction(String accountNumber, String type, double amount, String description) {
        this("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), accountNumber,
                type, amount, description, LocalDateTime.now(), "SUCCESS");
    }

    public Transaction(String id, String accountNumber, String type, double amount, String description,
                       LocalDateTime dateTime, String status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.dateTime = dateTime == null ? LocalDateTime.now() : dateTime;
        this.status = status == null ? "SUCCESS" : status;
    }

    public String getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getDateTimeText() { return dateTime.format(DISPLAY_FORMAT); }
    public String getStatus() { return status; }
}
