package org.example;

import org.example.Transaction.Transaction;
import org.example.Transaction.TransactionException;

import java.util.concurrent.BlockingQueue;

class Cachier extends Thread {

    private final int id;
    private final Bank bank;
    private volatile boolean active = true;

    public Cachier(int id, Bank bank) {
        this.id = id;
        this.bank = bank;
    }

    public void stopJob() {
        this.active = false;
    }

    @Override
    public void run() {
        bank.notifyObservers("Касса " + id + " начала работу");
        try {
            while (active) {
                bank.notifyObservers("Касса " + id + " получает транзакцию для обработки");
                Transaction transaction = bank.getTransactionQueue().take();
                bank.notifyObservers("Касса " + id + " получена транзакция " + transaction.getTransId());
                /* Обработка транзакции */
                try {
                    bank.notifyObservers("Касса " + id + " Транзакция " + transaction.getTransId() + " начала выполнение");
                    transaction.execute(bank);
                } catch (TransactionException e) {
                    bank.notifyObservers(e.getMessage());
                }
                bank.notifyObservers("Касса " + id + " Транзакция " + transaction.getTransId() + " завершила выполнение");

                if (Thread.currentThread().isInterrupted()) {
                    bank.notifyObservers("Касса " + id + " обрабатывает событие Interrupt");
                    active = false;
                    break;
                }

            }
        } catch (InterruptedException e) {
            bank.notifyObservers("Касса " + id + " обрабатывает событие Interrupt");
        }
        bank.notifyObservers("Касса " + id + " завершила работу");
    }

}
