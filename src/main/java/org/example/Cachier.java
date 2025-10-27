package org.example;

import org.example.Transaction.Transaction;
import org.example.Transaction.TransactionException;

class Cachier extends Thread {

    private final int id;
    private final Bank bank;

    public Cachier(int id, Bank bank) {
        this.id = id;
        this.bank = bank;
    }

    @Override
    public void run() {

        //Transaction transaction;

        bank.notifyObservers("Касса " + id + " начала работу");

        try {
            while (bank.getTransactionQueue().isEmpty() != true) {
                if (Thread.currentThread().isInterrupted()) {
                    bank.notifyObservers("Касса " + id + " обрабатывает событие Interrupt");
                    break;
                }
            //while ((transaction = bank.getTransactionQueue().poll()) != null) {
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
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bank.notifyObservers("Касса " + id + " завершила работу");
    }

}
