package org.example.Transaction;

import org.example.Bank;

public class EndTrans implements Transaction{
    private final int transId;

    public EndTrans(int transId) {
        this.transId = transId;
    }

    @Override
    public int getTransId() {
        return transId;
    }

    @Override
    public void execute(Bank bank) throws TransactionException {
        bank.notifyObservers("Транзакция " + transId + " - достигнут конец очереди");
        Thread.currentThread().interrupt();
        bank.getCountDownLatch().countDown();
    }
}
