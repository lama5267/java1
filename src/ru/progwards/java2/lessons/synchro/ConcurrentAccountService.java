package progwards.java2.lessons.synchro;

import app.Store;
import app.model.Account;
import app.service.AccountService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentAccountService implements AccountService {
    private FileStoreService service;

    public ConcurrentAccountService(FileStoreService fileStoreService) {
        this.service = fileStoreService;
    }

    public Account get(String id){
        return service.get(id);
    }

    @Override
    public  double balance(Account account) {
        return service.get(account.getId()).getAmount();
    }

    @Override
    public void deposit(Account account, double amount) {
        Account accountFound = service.get(account.getId());
        double ammountFound = accountFound.getAmount() + amount;

        accountFound.setAmount(ammountFound);
        service.update(accountFound);
    }

    @Override
    public void withdraw(Account account, double amount)  {
        Account accountFound = service.get(account.getId());
        double ammountFound = accountFound.getAmount() - amount;

        accountFound.setAmount(ammountFound);
        service.update(accountFound);
    }

    @Override
    public void transfer(Account from, Account to, double amount)  {
        withdraw(from, amount);
        deposit(to, amount);
    }

    public static void main(String[] args) throws IOException {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        FileStoreService service = new FileStoreService("d:/java/account.csv", lock);
        Files.writeString(service.path, "");
        ConcurrentAccountService accountService = new ConcurrentAccountService(service);

        Store.getStore().forEach((key, account) -> {
            service.insert(account);
        });

        Collection<Account> list = service.get();
        list.forEach(value -> System.out.println(value));

        Account account1 = new Account();
        Account account2 = new Account();
        for (Account value: list) {
            if (value.getHolder().compareTo("Account_1") == 0)
                account1 = value;

            if (value.getHolder().compareTo("Account_2") == 0)
                account2 = value;
        }

        accountService.transfer(account1, account2, 100000);

        System.out.println("-----------------------------------------------------");

        list = service.get();
        list.forEach(value -> System.out.println(value));
    }

}