package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;

public class PersistentExpenseManager extends ExpenseManager{
    private final Context context;

    public PersistentExpenseManager(Context context) throws ExpenseManagerException {
        super();
        this.context=context;
        setup();
    }



    @Override
    public void setup( ) throws ExpenseManagerException {
        TransactionDAO persistentTransactionDAO=new PersistentTransacTionDAO(context);
        setTransactionsDAO(persistentTransactionDAO);
        AccountDAO persistentAccountDAO=new PersistentAccountDAO(context);
        setAccountsDAO(persistentAccountDAO);
    }
}
