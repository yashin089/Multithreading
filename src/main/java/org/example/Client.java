package org.example;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client {
    private final int id;
    private double balance;
    private final String currency;
    private final Lock lock;

    public Client(int id, double balance, String currency) {
        this.id = id;
        this.balance = balance;
        this.currency = currency;
        this.lock = new ReentrantLock();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Lock getLock() {
        return lock;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
