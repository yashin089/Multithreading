package org.example;

class Logger implements Observer {
    @Override
    public void update(String message) {
// Здесь ваш код для логгирования, например:
        System.out.printf("Log: %s", message);
    }
}
