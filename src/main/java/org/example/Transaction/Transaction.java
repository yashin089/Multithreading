package org.example.Transaction;

import org.example.Bank;

public interface Transaction {
    public int getTransId();
    public void execute(Bank bank) throws TransactionException;
}
