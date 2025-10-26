package org.example;

public class TransferFundsTrans implements Transaction{
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

    public int getTransId() {
        return transId;
    }

    public double getAmount() {
        return amount;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    @Override
    public void execute(Bank bank) throws TransactionException {
        double resultAmount;

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

        while (true) {
            if (sender.getLock().tryLock()) {
                if (receiver.getLock().tryLock()) {
                    try {
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
