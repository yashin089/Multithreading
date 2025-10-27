package org.example.Transaction;

import org.example.Bank;
import org.example.Client;

public class ExchangeCurrencyTrans implements Transaction {
    private final int transId;
    private final int clientId;
    private final String fromCurrency;
    private final String toCurrency;
    private final double amount;

    public ExchangeCurrencyTrans(int transId, int clientId, String fromCurrency, String toCurrency, double amount) {
        this.transId = transId;
        this.clientId = clientId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
    }

    @Override
    public int getTransId() {
        return transId;
    }

    @Override
    public void execute(Bank bank) throws TransactionException {
        Client client;
        double exchRateToCurr;
        double resultAmount;

        if (bank == null) {
            throw new TransactionException("При выполнении тразакции " + transId + " не удалось определить банк");
        }

        client = bank.getClient(clientId);
        if (client == null) {
            throw new TransactionException("При выполнении тразакции " + transId + " не удалось определить клиента");
        }

        while (true) {
            if (client.getLock().tryLock()) {
                try {
                    if (client.getBalance() < amount) {
                        throw new TransactionException("При выполнении тразакции " + transId + " у клиента " + client.getId() + " оказалось недостаточно средств");
                    }
                    try {
                        exchRateToCurr = bank.getExchangeRates().get(toCurrency);
                        resultAmount = amount / exchRateToCurr;
                    } catch (Exception e) {
                        throw new TransactionException("При выполнении тразакции " + transId + " не удалось определить курс");
                    }
                    client.setBalance(client.getBalance() - amount);
                    bank.replaceClient(client);
                    bank.notifyObservers("Клиент " + client.getId() + " обменял " + amount + " " + fromCurrency + " на " + resultAmount + " " + toCurrency + " в рамках транзакции " + transId);
                } finally {
                    client.getLock().unlock();
                }
                break;
            }
        }
    }
}
