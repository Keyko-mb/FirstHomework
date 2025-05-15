package Pessimistic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("\nПессимистичный подход");
        testPessimistic();
    }

    public static void testPessimistic() {
        Account account1 = new Account(1, "Alex");
        account1.setBalance(BigDecimal.valueOf(200));
        Account account2 = new Account(2, "Kate");
        account2.setBalance(BigDecimal.valueOf(0));

        System.out.printf("Баланс %s: %f\n", account1.getOwner(), account1.getBalance());
        System.out.printf("Баланс %s: %f\n", account2.getOwner(), account2.getBalance());
        System.out.println("----------------------");

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Callable<Boolean>> tasks = new ArrayList<>();
        try {
            for (int i = 0; i < 100; i++) {
                tasks.add(() -> Service.transferMoney(account1, account2, BigDecimal.valueOf(1)));
            }
            var results = executorService.invokeAll(tasks);
            for (var result : results) {
                try {
                    result.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.printf("Баланс %s: %f\n", account1.getOwner(), account1.getBalance());
            System.out.printf("Баланс %s: %f\n", account2.getOwner(), account2.getBalance());
        } catch (Exception e) {
            System.out.println("Ошибка при переводе: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

//        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            futures.add(CompletableFuture.supplyAsync(() -> Service.transferMoney(account1, account2, 1)));
//        }
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                .exceptionally(ex -> {
//                    System.out.println("Ошибка при переводе - " + ex.getMessage());
//                    return null;
//                })
//                .thenRun(() -> {
//                    System.out.printf("Баланс %s: %f\n", account1.getOwner(), account1.getBalance());
//                    System.out.printf("Баланс %s: %f\n", account2.getOwner(), account2.getBalance());
//                })
//                .join();
}