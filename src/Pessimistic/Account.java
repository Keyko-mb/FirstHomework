package Pessimistic;

import java.math.BigDecimal;

public class Account {
    private final long id;
    private final String owner;
    private BigDecimal balance;
    private final Object lock = new Object();

    public Account(long id, String owner) {
        this.id = id;
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public Object getLock() {
        return lock;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void increaseBalance(BigDecimal sum) {
        synchronized (lock) {
            balance = balance.add(sum);
        }
    }

    public void reduceBalance(BigDecimal sum) {
        synchronized (lock) {
            balance = balance.subtract(sum);
        }
    }

    @Override
    public String toString() {
        return "Pessimistic.Account{" +
                "owner='" + owner + '\'' +
                ", balance=" + balance +
                '}';
    }
}
