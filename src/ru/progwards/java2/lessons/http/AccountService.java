package progwards.java2.lessons.http;

public interface AccountService {

    double balance(Account account);
    void deposit(Account account, double amount);
    void withdraw(Account account, double amount);
    void transfer(Account from, Account to, double amount);

}
