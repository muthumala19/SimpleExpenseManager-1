package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.accountNo;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.amount;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.date;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.expenseType;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteManager.transactionTable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final SQLiteManager manager;
    private SQLiteDatabase database;

    public PersistentTransactionDAO(Context context) {
        manager = new SQLiteManager(context);
    }

    @Override
    public void logTransaction(Date dateT, String accNo, ExpenseType type, double amnt) {
        database = manager.getWritableDatabase();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues contentValues = new ContentValues();
        contentValues.put(date, dateFormat.format(dateT));
        contentValues.put(accountNo, accNo);
        contentValues.put(expenseType, String.valueOf(type));
        contentValues.put(amount, amnt);
        database.insert(transactionTable, null, contentValues);
        database.close();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactions = new ArrayList<>();
        database = manager.getReadableDatabase();
        String[] columns = {date, accountNo, expenseType, amount};
        Cursor cursor = database.query(transactionTable, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String tempDate0 = cursor.getString(cursor.getColumnIndex(date));
            @SuppressLint("SimpleDateFormat") Date tempDate1 = new SimpleDateFormat("dd-MM-yyyy").parse(tempDate0);

            String accNo = cursor.getString(cursor.getColumnIndex(accountNo));

            String type = cursor.getString(cursor.getColumnIndex(expenseType));
            ExpenseType eType = ExpenseType.valueOf(type);

            double amnt = cursor.getDouble(cursor.getColumnIndex(amount));

            Transaction transaction = new Transaction(tempDate1, accNo, eType, amnt);
            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactions = new ArrayList<>();
        database = manager.getReadableDatabase();
        String[] columns = {date, accountNo, expenseType, amount};
        Cursor cursor = database.query(transactionTable, columns, null, null, null, null, null);
        int count = cursor.getCount();
        while (cursor.moveToNext()) {
            String tempDate0 = cursor.getString(cursor.getColumnIndex(date));
            @SuppressLint("SimpleDateFormat") Date tempDate1 = new SimpleDateFormat("dd-MM-yyyy").parse(tempDate0);

            String accNo = cursor.getString(cursor.getColumnIndex(accountNo));

            String type = cursor.getString(cursor.getColumnIndex(expenseType));
            ExpenseType eType = ExpenseType.valueOf(type);

            double amnt = cursor.getDouble(cursor.getColumnIndex(amount));

            Transaction transaction = new Transaction(tempDate1, accNo, eType, amnt);
            transactions.add(transaction);
        }
        cursor.close();
        if (count <= limit) return transactions;
        return transactions.subList(count - limit, count);
    }
}
