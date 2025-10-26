package org.example;


import java.util.List;
import java.util.concurrent.*;

public class Bank {

    private final ConcurrentMap<Integer, Client> clients = new ConcurrentHashMap<>();
    private final BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<>();
    private final ConcurrentMap<String, Double> exchangeRates = new ConcurrentHashMap<>();
    private List<Observer> observers = new CopyOnWriteArrayList<>();

    public Bank() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() -> {
            /* Здесь обновляются курсы валют */
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void addClient(Client client) {
        clients.put(client.getId(), client);
    }

    public Client getClient(int clientId) {
        return clients.get(clientId);
    }

    public void replaceClient(Client client) {
        clients.replace(client.getId(), client);
    }

    public BlockingQueue<Transaction> getTransactionQueue() {
        return transactions;
    }

    void addObserver(Observer observer) {
        observers.add(observer);
    }

    void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }

}
