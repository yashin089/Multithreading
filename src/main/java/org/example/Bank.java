package org.example;


import org.example.Transaction.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Bank {

    private final ConcurrentMap<Integer, Client> clients = new ConcurrentHashMap<>();
    private final BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<>();
    private final ConcurrentMap<String, Double> exchangeRates = new ConcurrentHashMap<>();
    private final List<Observer> observers = new CopyOnWriteArrayList<>();
    private final List<Cachier> cachiers = new ArrayList<>();
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public Bank() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        executor.scheduleAtFixedRate(() -> {
            double rateEUR = Math.random() * 100;
            double rateUSD = Math.random() * 100;
            exchangeRates.put("EUR", rateEUR);
            exchangeRates.put("USD", rateUSD);
            this.notifyObservers("Произошло обновление курса валют " + exchangeRates);
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

    public ConcurrentMap<String, Double> getExchangeRates() {
        return exchangeRates;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }

    public void addCachier(Cachier cachier) {
        cachiers.add(cachier);
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void star() {
        if (cachiers.isEmpty()) {
            return;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(cachiers.size());
        for (Cachier cachier : cachiers) {
            executorService.execute(cachier);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.notifyObservers("Банк завершает работу");
        for (Cachier cachier : cachiers) {
            cachier.stopJob();
        }

        executorService.shutdownNow();
    }
}
