package Pessimistic;

import java.math.BigDecimal;

public class Service {
    public static boolean transferMoney(Account from, Account to, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(0)) <= 0) throw new IllegalArgumentException("Нельзя перевести отрицательную сумму");
        boolean fromIsFirst = from.getId() < to.getId();
        Object firstLock = fromIsFirst ? from.getLock() : to.getLock();
        Object secondLock = fromIsFirst ? to.getLock() : from.getLock();
        synchronized (firstLock) {
            if (fromIsFirst && from.getBalance().compareTo(amount) < 0)
                return false;
            synchronized (secondLock) {
                if (!fromIsFirst && to.getBalance().compareTo(amount) < 0)
                    return false;
                from.reduceBalance(amount);
                to.increaseBalance(amount);
            }
        }
        return true;
    }
}
