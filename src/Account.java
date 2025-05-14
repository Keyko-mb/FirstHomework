import java.util.concurrent.atomic.AtomicInteger;

public class Account {
    private final String owner;
    private int balance;
    private AtomicInteger atomicBalance;
    private final boolean isOptimistic;

    public Account(String owner, boolean isOptimistic) {
        this.owner = owner;
        this.isOptimistic = isOptimistic;
        if (isOptimistic) atomicBalance = new AtomicInteger();
    }

    public String getOwner() {
        return owner;
    }

    public int getBalance() {
        if (isOptimistic) return atomicBalance.get();
        else return balance;
    }

    public void setBalance(int balance) {
        if (isOptimistic) atomicBalance.set(balance);
        else this.balance = balance;
    }

    public void increaseBalance(int sum) {
        if (isOptimistic) atomicBalance.addAndGet(sum);
        else synchronized (this) {
            balance = balance + sum;
        }
    }

    public void reduceBalance(int sum) {
        if (isOptimistic) {
            if (atomicBalance.get() < sum)
                throw new IllegalArgumentException("Недостаточно денег на балансе");
            atomicBalance.getAndUpdate(balance -> balance - sum);
        } else {
            if (balance < sum)
                throw new IllegalArgumentException("Недостаточно денег на балансе");
            balance = balance - sum;
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "owner='" + owner + '\'' +
                ", balance=" + balance +
                '}';
    }
}
