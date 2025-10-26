package org.example;

public class WithdrawTrans implements Transaction {
    private final int transId;
    private final int clientId;
    private final double amount;

    public WithdrawTrans(int transId, int clientId, double amount) {
        this.transId = transId;
        this.clientId = clientId;
        this.amount = amount;
    }

    public int getTransId() {
        return transId;
    }

    public int getClientId() {
        return clientId;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public void execute(Bank bank) throws TransactionException {
        Client client;
        double resultAmount;
        String logMessage;

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
                    resultAmount = client.getBalance() - amount;
                    client.setBalance(resultAmount);
                    bank.replaceClient(client);
                    bank.notifyObservers("Клиент " + client.getId() + "сделал снятие денежных средств на " + amount + "в рамках транзакции " + transId);
                } finally {
                    client.getLock().unlock();
                }
                break;
            }
        }
    }
}
