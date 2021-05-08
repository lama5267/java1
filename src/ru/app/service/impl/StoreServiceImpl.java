package app.service.impl;

import app.Store;
import app.model.Account;
import app.service.StoreService;

import java.util.Collection;
import java.util.List;

public class StoreServiceImpl implements StoreService {
    @Override
    public Account get(String id) {
        Account account = Store.getStore().get(id);
        if (account == null){
            throw new RuntimeException("Account not found by id:"+id);
        }
        return account;
    }

    @Override
    public Collection<Account> get() {
        if (Store.getStore().size() == 0) {
            throw new RuntimeException("Store is empty");
        }
        return Store.getStore().values();
    }

    @Override
    public void delete(String id) {
        if (Store.getStore().get(id) == null) {
            throw new RuntimeException("Account not found by id:"+id);
        }
        synchronized (Store.getStore()) {
            Store.getStore().remove(id);
        }
    }

    @Override
    public void insert(Account account) {
        synchronized (Store.getStore()) {
            Store.getStore().put(account.getId(), account);
        }
    }

    @Override
    public void update(Account account) {
        if (Store.getStore().get(account.getId()) == null){
            throw new RuntimeException("Account not found by id:"+account.getId());
        }
        synchronized (Store.getStore()) {
            this.insert(account);
        }
    }
}
