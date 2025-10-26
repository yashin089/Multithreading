package org.example;

interface Transaction {
    void execute(Bank bank) throws TransactionException;
}
