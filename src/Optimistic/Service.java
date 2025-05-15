package Optimistic;

import java.math.BigDecimal;

public class Service {
    public static boolean transferMoney(Account from, Account to, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(0)) <= 0)
            throw new IllegalArgumentException("Нельзя перевести отрицательную сумму");

        int reduceRetry = 10;

        while (reduceRetry > 0) {
            boolean fromSuccess = from.reduceBalance(amount);
            if (fromSuccess) {
                boolean toSuccess = to.increaseBalance(amount);
                if (toSuccess)
                    return true;
                else {
                    int rollbackRetry = 5;
                    boolean rollbackSuccess = false;
                    while (!rollbackSuccess) {
                        if (from.increaseBalance(amount)) rollbackSuccess = true;
                        else rollbackRetry--;

                        if (rollbackRetry <= 0)
                            throw new RuntimeException("Критическая ошибка: операция не выполнена, но с аккаунта были списаны деньги");

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                reduceRetry--;
            }
        }
        throw new RuntimeException("Операция не выполнена");
    }
}
