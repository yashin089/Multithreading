package org.example.Transaction;

import org.example.Bank;
import org.example.Client;

public class TransferFundsTrans implements Transaction {
    private final int transId;
    private final int senderId;
    private final int receiverId;
    private final double amount;

    public TransferFundsTrans(int transId, int senderId, int receiverId, double amount) {
        this.transId = transId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    @Override
    public int getTransId() {
        return transId;
    }

    @Override
    public void execute(Bank bank) throws TransactionException {
        if (bank == null) {
            throw new TransactionException("При выполнении тразакции " + transId + " не удалось определить банк");
        }

        Client sender = bank.getClient(senderId);
        if (sender == null) {
            throw new TransactionException("При выполнении тразакции " + transId + " не удалось определить клиента отправителя");
        }

        Client receiver = bank.getClient(receiverId);
        if (receiver == null) {
            throw new TransactionException("При выполнении тразакции " + transId + " не удалось определить клиента получателя");
        }

        if (sender.getId() == receiver.getId()) {
            throw new TransactionException("При выполнении тразакции " + transId + " клиент " + sender.getId() + " попытался перевести деньги сам себе");
        }

        while (true) {
            if (sender.getLock().tryLock()) {
                if (receiver.getLock().tryLock()) {
                    try {
                        if (sender.getBalance() < amount) {
                            throw new TransactionException("При выполнении тразакции " + transId + " у клиента " + sender.getId() + " оказалось недостаточно средств");
                        }
                        sender.setBalance(sender.getBalance() - amount);
                        receiver.setBalance(sender.getBalance() + amount);
                        bank.replaceClient(sender);
                        bank.replaceClient(receiver);
                        bank.notifyObservers("Клиент " + sender.getId() + "сделал перевод денежных средств в размере " + amount + " клиенту " + receiver.getId() + " в рамках транзакции " + transId);
                    } finally {
                        receiver.getLock().unlock();
                        sender.getLock().unlock();
                    }
                    break;
                } else {
                    sender.getLock().unlock();
                }
            }
        }
    }
}
