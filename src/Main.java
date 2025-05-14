import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Оптимистичный подход");
        testOptimistic();
        System.out.println("\nПессимистичный подход");
        testPessimistic();
    }

    public static void testOptimistic() {
        Account account1 = new Account("Alex", true);
        account1.setBalance(200);
        Account account2 = new Account("Kate", true);
        account2.setBalance(0);

        System.out.printf("Баланс %s: %d\n", account1.getOwner(), account1.getBalance());
        System.out.printf("Баланс %s: %d\n", account2.getOwner(), account2.getBalance());
        System.out.println("----------------------");

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Callable<Boolean>> tasks = new ArrayList<>();
        try {
            for (int i = 0; i < 100; i++) {
                tasks.add(() -> Service.transferMoney(account1, account2, 1));
            }
            var results = executorService.invokeAll(tasks);
            for (var result : results) {
                try {
                    result.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.printf("Баланс %s: %d\n", account1.getOwner(), account1.getBalance());
            System.out.printf("Баланс %s: %d\n", account2.getOwner(), account2.getBalance());
        } catch (Exception e) {
            System.out.println("Ошибка при переводе: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    public static void testPessimistic() {
        Account account1 = new Account("Alex", false);
        account1.setBalance(200);
        Account account2 = new Account("Kate", false);
        account2.setBalance(0);

        System.out.printf("Баланс %s: %d\n", account1.getOwner(), account1.getBalance());
        System.out.printf("Баланс %s: %d\n", account2.getOwner(), account2.getBalance());
        System.out.println("----------------------");

        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> Service.transferMoney(account1, account2, 1)));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    System.out.println("Ошибка при переводе - " + ex.getMessage());
                    return null;
                })
                .thenRun(() -> {
                    System.out.printf("Баланс %s: %d\n", account1.getOwner(), account1.getBalance());
                    System.out.printf("Баланс %s: %d\n", account2.getOwner(), account2.getBalance());
                })
                .join();
    }
}