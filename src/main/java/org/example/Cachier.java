package org.example;

import java.util.concurrent.CountDownLatch;

class Cachier extends Thread {

    private int id;
    private Bank bank;

    public Cachier(int id, Bank bank) {
        this.id = id;
        this.bank = bank;
    }

    @Override
    public void run() {

        boolean active = true;

        try {
            while (active) {
                Transaction transaction = bank.getTransactionQueue().take();
                /* Обработка транзакции */
                try {
                    transaction.execute(bank);
                } catch (TransactionException e) {
                    bank.notifyObservers(e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
