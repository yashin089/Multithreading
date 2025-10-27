package org.example;

import org.example.Transaction.*;

public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger();
        Bank bank = new Bank();

        //Вешаем логер
        bank.addObserver(logger);

        //Создаем клиентов для банка
        for (int i = 1; i <= 10; i++) {
            bank.addClient(new Client(i, (i * 1000), "RUB"));
        }

        //Создаем очередь транзакций
        for (int i = 1; i <= 10; i++) {
            int client1 = (int) (Math.random() * 10);
            int client2 = (int) (Math.random() * 10);
            int operation = (int) (Math.random() * 4);
            switch (operation) {
                case 1:
                    bank.getTransactionQueue().add(new DepositTrans(i, client1, (i * 50)));
                    break;
                case 2:
                    bank.getTransactionQueue().add(new WithdrawTrans(i, client1, (i * 150)));
                    break;
                case 3:
                    bank.getTransactionQueue().add(new ExchangeCurrencyTrans(i, client1, "RUB", "EUR", (i * 150)));
                    //bank.getTransactionQueue().add(new ExchangeCurrencyTrans(i, client1, "RUB", "USD", (i * 50)));
                    break;
                case 4:
                    bank.getTransactionQueue().add(new TransferFundsTrans(i, client1, client2, (i * 50)));
                    //bank.getTransactionQueue().add(new TransferFundsTrans(i, client2, client1, (i * 150)));
                    break;
            }
        }

        //Создаем 5 касс
        for (int i = 1; i <= 5; i++) {
            bank.addCachier(new Cachier(i, bank));
        }

        bank.star();
    }
}