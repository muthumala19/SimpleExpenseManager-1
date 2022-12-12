package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.accountHolderName;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.accountNo;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.accountTable;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.balance;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.bankName;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentAccountDAO implements AccountDAO {
    private final SQLiteManager manager;
    private SQLiteDatabase database;

    public PersistentAccountDAO(Context context) {
        manager = new SQLiteManager(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        database = manager.getReadableDatabase();
        String[] columns = {accountNo};
        Cursor cursor = database.query(accountTable, columns, null, null, null, null, null);
        List<String> accNumbers = new ArrayList<>();
        while (cursor.moveToNext()) {
            String accNumber = cursor.getString(cursor.getColumnIndexOrThrow(accountNo));
            accNumbers.add(accNumber);
        }
        cursor.close();
        return accNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();
        database = manager.getReadableDatabase();
        String[] columns = {accountNo, bankName, accountHolderName, balance};
        Cursor cursor = database.query(accountTable, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Account account = new Account(cursor.getString(cursor.getColumnIndex(accountNo)), cursor.getString(cursor.getColumnIndex(bankName)), cursor.getString(cursor.getColumnIndex(accountHolderName)), cursor.getDouble(cursor.getColumnIndex(balance)));
            accounts.add(account);
        }
        cursor.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accNo) throws InvalidAccountException {
        Account acc;
        database = manager.getReadableDatabase();
        String[] columns = {accountNo, bankName, accountHolderName, balance};
        String condition = accountNo + " = ?";
        String[] conditionArgs = {accNo};
        Cursor cursor = database.query(accountTable, columns, condition, conditionArgs, null, null, null);
        if (cursor == null) {
            throw new InvalidAccountException("Account no : " + accNo + " is Invalid!");
        } else {
            cursor.moveToFirst();
            acc = new Account(accNo, cursor.getString(cursor.getColumnIndex(bankName)), cursor.getString(cursor.getColumnIndex(accountHolderName)), cursor.getDouble(cursor.getColumnIndex(balance)));
        }
        cursor.close();
        return acc;
    }

    @Override
    public void addAccount(Account account) {
        database = manager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(accountNo, account.getAccountNo());
        values.put(bankName, account.getBankName());
        values.put(accountHolderName, account.getAccountHolderName());
        values.put(balance, account.getBalance());
        database.insert(accountTable, null, values);
        database.close();
    }

    @Override
    public void removeAccount(String accNo) {
        database = manager.getWritableDatabase();
        database.delete(accountTable, accountNo + "=?", new String[]{accNo});
        database.close();
    }

    @Override
    public void updateBalance(String accNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        database = manager.getWritableDatabase();
        String[] columns = {balance};
        String condition = accountNo + " = ?";
        String[] conditionArgs = {accNo};
        Cursor cursor = database.query(accountTable, columns, condition, conditionArgs, null, null, null);

        double bal;
        if (cursor.moveToFirst()) {
            bal = cursor.getDouble(0);
        } else {
            throw new InvalidAccountException("Account no : " + accNo + " is Invalid!");
        }

        ContentValues values = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                values.put(balance, bal - amount);
                break;
            case INCOME:
                values.put(balance, bal + amount);
                break;
        }
        database.update(accountTable, values, accountNo + " = ?", new String[]{accNo});
        cursor.close();
        database.close();

    }
}
