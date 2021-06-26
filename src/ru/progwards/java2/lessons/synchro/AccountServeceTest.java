package progwards.java2.lessons.synchro;

import app.model.Account;

import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ThreadTest extends Thread {
    ConcurrentAccountService accountService;
    FileStoreService service;
    ThreadTest(FileStoreService service){
        this.service = service;
        this.accountService = new ConcurrentAccountService(service);
    }
    @Override
    public void run() {
        Collection<Account> collection = service.get();

        for (int i = 0; i < 10; i ++)
            for (Account value: collection) {
                accountService.deposit(value, 500);
            }

    }
}

public class AccountServeceTest{
    public static void main(String[] args) {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        FileStoreService service = new FileStoreService("d:/account.csv", lock);
        Thread[] myThread = new Thread[10];
        for (int i = 0; i < 10; i ++) {
            myThread[i] = new ThreadTest(service);
            myThread[i].start();
        }
    }
}
