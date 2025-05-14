public class Service {
    public static boolean transferMoney(Account from, Account to, int amount) {
        if (amount < 0) throw new IllegalArgumentException("Нельзя перевести отрицательную сумму");
        try {
            from.reduceBalance(amount);
            to.increaseBalance(amount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
