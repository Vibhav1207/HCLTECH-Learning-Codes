import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class FileService {
    private final File accountsFile;
    private final File transactionsFile;

    public FileService() { this("accounts.txt", "transactions.txt"); }

    public FileService(String accountsPath, String transactionsPath) {
        accountsFile = new File(accountsPath);
        transactionsFile = new File(transactionsPath);
    }

    public ArrayList<BAcc> loadAccounts() {
        ArrayList<BAcc> accounts = new ArrayList<BAcc>();
        if (!accountsFile.exists()) return accounts;
        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] p = line.split("\\|", -1);
                    if (p.length != 8) continue;
                    String number = p[0];
                    String name = new String(Base64.getDecoder().decode(p[1]), StandardCharsets.UTF_8);
                    String type = p[2];
                    double balance = Double.parseDouble(p[3]);
                    LocalDateTime created = LocalDateTime.parse(p[4]);
                    byte[] salt = Base64.getDecoder().decode(p[5]);
                    byte[] hash = Base64.getDecoder().decode(p[6]);
                    String status = p[7];
                    if ("SAVINGS".equals(type)) {
                        accounts.add(new SavingAcc(number, name, balance, created, salt, hash, status));
                    } else if ("CURRENT".equals(type)) {
                        accounts.add(new CurrentAcc(number, name, balance, created, salt, hash, status));
                    }
                } catch (RuntimeException ignored) {
                    // A damaged row does not prevent healthy accounts from loading.
                }
            }
        } catch (IOException ignored) {
            // The service starts with an empty collection and can recreate the file.
        }
        return accounts;
    }

    public ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        if (!transactionsFile.exists()) return transactions;
        try (BufferedReader reader = new BufferedReader(new FileReader(transactionsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] p = line.split("\\|", -1);
                    if (p.length != 7) continue;
                    String description = new String(Base64.getDecoder().decode(p[4]), StandardCharsets.UTF_8);
                    transactions.add(new Transaction(p[0], p[1], p[2], Double.parseDouble(p[3]), description,
                            LocalDateTime.parse(p[5]), p[6]));
                } catch (RuntimeException ignored) { }
            }
        } catch (IOException ignored) { }
        return transactions;
    }

    public synchronized void saveAccounts(Collection<BAcc> accounts) throws IOException {
        ensureParent(accountsFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsFile))) {
            for (BAcc account : accounts) {
                writer.write(account.getAccountNumber());
                writer.write('|');
                writer.write(Base64.getEncoder().encodeToString(account.getHolderName().getBytes(StandardCharsets.UTF_8)));
                writer.write('|');
                writer.write(account.getAccountType());
                writer.write('|');
                writer.write(String.format(Locale.US, "%.2f", account.getBalance()));
                writer.write('|');
                writer.write(account.getCreatedAtText());
                writer.write('|');
                writer.write(Base64.getEncoder().encodeToString(account.getPinSalt()));
                writer.write('|');
                writer.write(Base64.getEncoder().encodeToString(account.getPinHash()));
                writer.write('|');
                writer.write(account.getStatus());
                writer.newLine();
            }
        }
    }

    public synchronized void saveTransactions(Collection<Transaction> transactions) throws IOException {
        ensureParent(transactionsFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsFile))) {
            for (Transaction transaction : transactions) {
                String safeDescription = Base64.getEncoder().encodeToString(
                        transaction.getDescription().getBytes(StandardCharsets.UTF_8));
                writer.write(String.join("|", transaction.getId(), transaction.getAccountNumber(),
                        transaction.getType(), String.format(Locale.US, "%.2f", transaction.getAmount()),
                        safeDescription, transaction.getDateTime().toString(), transaction.getStatus()));
                writer.newLine();
            }
        }
    }

    private void ensureParent(File file) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Could not create data directory");
        }
    }
}
