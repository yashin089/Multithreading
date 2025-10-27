package org.example;

import org.example.Transaction.*;

public class Main {

    private static final int CLIENT_COUNT = 10;
    private static final int TRANSACTION_COUNT = 100;
    private static final int CACHIER_COUNT = 5;

    private static int getRandom(int max) {

        int range = (max - 1) + 1;
        return (int) ((range * Math.random()) + 1);
    }

    public static void main(String[] args) {
        Logger logger = new Logger();
        Bank bank = new Bank();

        //Вешаем логер
        bank.addObserver(logger);

        //Создаем клиентов для банка
        for (int i = 1; i <= CLIENT_COUNT; i++) {
            bank.addClient(new Client(i, (i * 1000), "RUB"));
        }

        //Создаем очередь транзакций
        for (int i = 1; i <= TRANSACTION_COUNT; i++) {
            int client1 = getRandom(CLIENT_COUNT);
            int client2 = getRandom(CLIENT_COUNT);
            int operation = getRandom(4);
            switch (operation) {
                case 1:
                    bank.getTransactionQueue().add(new DepositTrans(i, client1, (i * 50)));
                    break;
                case 2:
                    bank.getTransactionQueue().add(new WithdrawTrans(i, client1, (i * 150)));
                    break;
                case 3:
                    bank.getTransactionQueue().add(new ExchangeCurrencyTrans(i, client1, "RUB", "EUR", (i * 150)));
                    break;
                case 4:
                    bank.getTransactionQueue().add(new TransferFundsTrans(i, client1, client2, (i * 50)));
                    break;
            }
        }
        bank.getTransactionQueue().add(new EndTrans((TRANSACTION_COUNT + 1)));

        //Создаем кассы
        for (int i = 1; i <= CACHIER_COUNT; i++) {
            bank.addCachier(new Cachier(i, bank));
        }

        bank.star();
    }
}