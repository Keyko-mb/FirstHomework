package Optimistic;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class Account {
    private final long id;
    private final String owner;
    private final AtomicReference<BigDecimal> balance;

    public Account(long id, String owner) {
        this.id = id;
        this.owner = owner;
        balance = new AtomicReference<>(BigDecimal.valueOf(0));
    }

    public String getOwner() {
        return owner;
    }

    public AtomicReference<BigDecimal> getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance.set(balance);
    }

    public boolean increaseBalance(BigDecimal sum) {
        while (true) {
            BigDecimal currentBalance = balance.get();
            if (balance.compareAndSet(currentBalance, currentBalance.add(sum))) return true;
        }
    }

    public boolean reduceBalance(BigDecimal sum) {
        while (true) {
            BigDecimal currentBalance = balance.get();
            if (currentBalance.compareTo(sum) < 0)
                return false;
            if (balance.compareAndSet(currentBalance, currentBalance.subtract(sum))) return true;
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
