package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteManager extends SQLiteOpenHelper {

    //Application details
    private static final int version=1;
    private static final String databaseName="200405B";

    //Table names
    public static final String accountTable="account";
    public static final String transactionTable="transactionT";

    //Column names of tables
    public static final String accountNo="accountNo";
    public static final String bankName="bankName";
    public static final String accountHolderName="accountHolderName";
    public static final String balance="balance";
    public static final String id="id";
    public static final String date="date";
    public static final String expenseType="expenseType";
    public static final String amount="amount";

    public SQLiteManager( Context context) {
        super(context, databaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + accountTable + "(" +
                accountNo + " TEXT primary key, " +
                bankName + " TEXT not null, " +
                accountHolderName + " TEXT not null, " +
                balance + " REAL not null)");

        sqLiteDatabase.execSQL("create table " + transactionTable + "(" +
                id + " INTEGER primary key autoincrement, " +
                date + " TEXT not null, " +
                expenseType + " TEXT not null, " +
                amount + " REAL not null, " +
                accountNo + " TEXT," +
                "foreign key (" + accountNo + ") references " + accountTable + "(" + accountNo + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int latestVersion, int previousVersion) {

        sqLiteDatabase.execSQL("drop table if exists " + accountTable);
        sqLiteDatabase.execSQL("drop table if exists " + transactionTable);
        onCreate(sqLiteDatabase);
    }
}
