# HDFC ATM Simulator

A complete Core Java desktop banking application built with Swing and AWT. All source files use the default package and are kept directly in this folder.

## Run

Open a terminal in `ATM-Simulator` and run:

```text
javac *.java
java Main
```

The application creates and updates `accounts.txt` and `transactions.txt` in the same folder. Missing files are created automatically. Invalid or damaged rows are skipped so the rest of the data remains usable.

## Administrator access

Use the **Admin login** button on the welcome screen.

```text
Username: admin
Password: admin123
```

The administrator can search accounts and view account and transaction summaries. PINs and password hashes are never displayed.

## Customer test walkthrough

1. Select **Create new account**, use a full name, four-digit PIN and an initial deposit of at least ₹500.
2. Sign in with the generated account number and PIN.
3. Try Deposit and Withdrawal using the quick amount buttons. Savings accounts retain a ₹500 minimum; Current accounts allow overdraft up to ₹10,000.
4. Create a second account and use Transfer. The receiver is looked up before confirmation and both customers receive a transaction entry.
5. Open Mini statement or Transaction history to search, filter and sort activity.
6. Use Account details and Change PIN. Close and restart the application to verify persistence.
7. Enter an incorrect PIN three times to verify the one-minute temporary lockout.

## Design and implementation

- `BAcc` is an abstract, encapsulated account base class. `SavingAcc` and `CurrentAcc` inherit it and override withdrawal rules, demonstrating inheritance and polymorphism.
- `TransactionOperations` is the banking interface. `AccountService` owns the `HashMap` account registry and transaction `ArrayList`, with generic collections, sorting, comparators and method references.
- `FileService` uses `File`, `FileReader`, `FileWriter`, `BufferedReader` and `BufferedWriter` for persistent text storage. Customer names and descriptions are Base64-encoded to keep the delimiter format safe.
- PINs are never stored in plaintext. Each account has a random salt and an iterative SHA-256 hash.
- Custom checked exceptions provide validation for invalid amounts, invalid PINs, missing accounts and insufficient funds.
- Swing frames use `CardLayout`, `BorderLayout`, `GridLayout` and `FlowLayout`. Reusable rounded cards, buttons, labels and logo painting live in `UIComponents`.
- Event handling uses `ActionListener`, lambda expressions and `MouseListener`. The application starts on the Swing Event Dispatch Thread through `SwingUtilities.invokeLater`.

## Data files

`accounts.txt` stores account number, encoded customer name, type, balance, creation time, salt, hash and status. `transactions.txt` stores transaction ID, account number, type, amount, encoded description, time and status. Do not edit these files while the application is running.
