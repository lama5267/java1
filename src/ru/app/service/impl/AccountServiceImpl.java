package app.service.impl;

import app.Store;
import app.model.Account;
import app.service.AccountService;
import app.service.StoreService;
import progwards.java2.lessons.synchro.InvalidPointerException;

import java.io.IOException;

public class AccountServiceImpl implements AccountService {
    private StoreService service;

    public AccountServiceImpl(){

    }

    public AccountServiceImpl(StoreService service){
        this.service = service;
    }

    @Override
    public double balance(Account account) {
        return account.getAmount();
    }

    @Override
    public void deposit(Account account, double amount)  {
        double sum = account.getAmount() + amount;
        synchronized (Store.getStore()) {
            account.setAmount(sum);
            try {
                service.update(account);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void withdraw(Account account, double amount) {
        double sum = account.getAmount() - amount;
        if (sum < 0) {
            throw new RuntimeException("Not enough money");
        }
        synchronized (Store.getStore()) {
            account.setAmount(sum);
            try {
                service.update(account);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void transfer(Account from, Account to, double amount) {
        double fromSum = from.getAmount() - amount;
        double toSum = to.getAmount() + amount;
        if (fromSum < 0) {
            throw new RuntimeException("Not enough money");
        }
        synchronized (Store.getStore()) {
            from.setAmount(fromSum);
            try {
                service.update(from);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidPointerException e) {
                e.printStackTrace();
            }
            to.setAmount(toSum);
            try {
                service.update(to);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidPointerException e) {
                e.printStackTrace();
            }
        }
    }
}