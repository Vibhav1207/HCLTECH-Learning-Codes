import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AccountService {
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin123";
    private final Map<String, BAcc> accounts = new HashMap<String, BAcc>();
    private final ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private final FileService fileService;
    private long nextAccountNumber = 10000001L;

    public AccountService() {
        fileService = new FileService();
        load();
    }

    private synchronized void load() {
        for (BAcc account : fileService.loadAccounts()) {
            accounts.put(account.getAccountNumber(), account);
            updateSequence(account.getAccountNumber());
        }
        transactions.addAll(fileService.loadTransactions());
    }

    private void updateSequence(String number) {
        try {
            String digits = number.replaceAll("\\D", "");
            if (!digits.isEmpty()) nextAccountNumber = Math.max(nextAccountNumber, Long.parseLong(digits) + 1);
        } catch (NumberFormatException ignored) { }
    }

    public synchronized BAcc register(String name, String pin, String type, double initialDeposit)
            throws InvalidAmountException {
        if (name == null || name.trim().length() < 2) throw new InvalidAmountException("Enter the customer's full name.");
        if (pin == null || !pin.matches("\\d{4}")) throw new InvalidAmountException("PIN must contain exactly four digits.");
        if (!"SAVINGS".equals(type) && !"CURRENT".equals(type)) throw new InvalidAmountException("Select a valid account type.");
        if (!Double.isFinite(initialDeposit) || initialDeposit < 500) {
            throw new InvalidAmountException("Initial deposit must be at least ₹500.");
        }
        String number;
        do { number = "NB" + nextAccountNumber++; } while (accounts.containsKey(number));
        BAcc account = "SAVINGS".equals(type)
                ? new SavingAcc(number, name.trim(), pin, initialDeposit)
                : new CurrentAcc(number, name.trim(), pin, initialDeposit);
        accounts.put(number, account);
        addTransaction(new Transaction(number, "DEPOSIT", initialDeposit, "Opening deposit"));
        persist();
        return account;
    }

    public synchronized BAcc login(String accountNumber, String pin) throws AccountNotFoundException, InvalidPinException {
        BAcc account = accounts.get(accountNumber == null ? "" : accountNumber.trim());
        if (account == null) throw new AccountNotFoundException("No account was found for that account number.");
        account.verifyPin(pin);
        return account;
    }

    public synchronized BAcc findAccount(String accountNumber) throws AccountNotFoundException {
        BAcc account = accounts.get(accountNumber == null ? "" : accountNumber.trim());
        if (account == null) throw new AccountNotFoundException("Account not found.");
        return account;
    }

    public synchronized void deposit(String accountNumber, double amount) throws Exception {
        BAcc account = findAccount(accountNumber);
        account.deposit(amount);
        addTransaction(new Transaction(accountNumber, "DEPOSIT", amount, "Cash deposit"));
        persist();
    }

    public synchronized void withdraw(String accountNumber, double amount) throws Exception {
        BAcc account = findAccount(accountNumber);
        account.withdraw(amount);
        addTransaction(new Transaction(accountNumber, "WITHDRAWAL", amount, "Cash withdrawal"));
        persist();
    }

    public synchronized void transfer(String fromNumber, String toNumber, double amount) throws Exception {
        if (fromNumber == null || fromNumber.equals(toNumber)) throw new InvalidAmountException("You cannot transfer money to the same account.");
        BAcc sender = findAccount(fromNumber);
        BAcc receiver = findAccount(toNumber);
        sender.withdraw(amount);
        try {
            receiver.deposit(amount);
        } catch (InvalidAmountException ex) {
            sender.deposit(amount);
            throw ex;
        }
        addTransaction(new Transaction(fromNumber, "TRANSFER SENT", amount, "Transfer to " + receiver.getHolderName() + " (" + toNumber + ")"));
        addTransaction(new Transaction(toNumber, "TRANSFER RECEIVED", amount, "Transfer from " + sender.getHolderName() + " (" + fromNumber + ")"));
        persist();
    }

    public synchronized void changePin(String accountNumber, String oldPin, String newPin) throws Exception {
        BAcc account = findAccount(accountNumber);
        account.verifyPin(oldPin);
        account.changePin(newPin);
        persist();
    }

    public synchronized List<Transaction> getTransactionsFor(String accountNumber) {
        return getTransactionsFor(accountNumber, Integer.MAX_VALUE);
    }

    public synchronized List<Transaction> getTransactionsFor(String accountNumber, int limit) {
        ArrayList<Transaction> result = new ArrayList<Transaction>();
        for (Transaction transaction : transactions) if (transaction.getAccountNumber().equals(accountNumber)) result.add(transaction);
        Collections.sort(result, Comparator.comparing(Transaction::getDateTime).reversed());
        if (limit >= 0 && result.size() > limit) return new ArrayList<Transaction>(result.subList(0, limit));
        return result;
    }

    public synchronized List<Transaction> getAllTransactions() {
        ArrayList<Transaction> result = new ArrayList<Transaction>(transactions);
        result.sort(Comparator.comparing(Transaction::getDateTime).reversed());
        return result;
    }

    public synchronized List<BAcc> getAllAccounts() {
        ArrayList<BAcc> result = new ArrayList<BAcc>(accounts.values());
        result.sort(Comparator.comparing(BAcc::getAccountNumber));
        return result;
    }

    public boolean authenticateAdmin(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    private void addTransaction(Transaction transaction) { transactions.add(transaction); }

    private synchronized void persist() {
        try {
            fileService.saveAccounts(accounts.values());
            fileService.saveTransactions(transactions);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not save banking data: " + ex.getMessage(), ex);
        }
    }
}
