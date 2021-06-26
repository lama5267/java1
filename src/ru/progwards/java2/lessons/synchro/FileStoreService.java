package progwards.java2.lessons.synchro;

import app.model.Account;
import app.service.StoreService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;

public class FileStoreService implements StoreService {
    //Предполагается, что данный класс создается один раз и вызывается потоками
    public Path path;
    private List<Account> collection = new ArrayList<>();
    private ReadWriteLock lock;

    public FileStoreService(String pathString, ReadWriteLock lock) {
        this.path = Paths.get(pathString);
        this.lock = lock;
        try {
            if (!Files.exists(path))
                Files.writeString(path, "");
            Files.lines(path).forEach(line -> collection.add(parseAccount(line)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Account get(String id) {
        lock.readLock().lock();
        for (Account account : collection)
            if (account != null && account.getId().compareTo(id) == 0) {
                lock.readLock().unlock();
                return account;
            }
        lock.readLock().unlock();
        return null;
    }

    @Override
    public Collection<Account> get() {
        return collection;
    }

    @Override
    public void delete(String id) throws IOException, InvalidPointerException {
        Account account = null;
        lock.readLock().lock();
        for (Account value : collection)
            if (value.getId().compareTo(id) == 0) {
                account = value;
                break;
            }
        lock.readLock().unlock();
        if (account == null)
            throw new InvalidPointerException("Аккаунт с id = " + id + " в базе данных отсутствует!");

        lock.writeLock().lock();
        collection.remove(account);
        lock.writeLock().lock();
        rewrite();
    }

    @Override
    public void insert(Account account) {
        lock.readLock().lock();
        for (Account value : collection)
            if (value.getId().compareTo(account.getId()) == 0) {
                lock.readLock().unlock();
                update(account);
                return;
            }
        lock.readLock().unlock();
        lock.writeLock().lock();
        collection.add(account);
        try {
            Files.writeString(path, account.toString(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
        lock.writeLock().unlock();
    }

    @Override
    public void update(Account account)  {
        boolean wasFound = false;
        lock.readLock().lock();
        for (int i = 0; i < collection.size(); i ++) {
            if (collection.get(i).getId().compareTo(account.getId()) == 0){
                lock.readLock().unlock();
                lock.writeLock().lock();
                collection.set(i, account);
                lock.writeLock().unlock();
                wasFound = true;
                break;
            }
        }
        if (!wasFound)
            try {
                throw new InvalidPointerException("Аккаунт с id = " + account.getId() + " в базе данных отсутствует!");
            } catch (InvalidPointerException e){
                e.printStackTrace();
            }
        rewrite();
    }

    private void rewrite()  {
        //Перезаписываем всю коллекцию в файл
        lock.writeLock().lock();
        try {
            Files.writeString(path, "");
            collection.forEach(value -> {
                try {
                    Files.writeString(path, value.toString(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        lock.writeLock().unlock();
    }

    private Account parseAccount(String accountString) {
        String[] line = accountString.split(",");
        line[0] = line[0].substring(3).replaceAll("'", "");
        line[1] = line[1].substring(8).replaceAll("'", "");
        line[2] = line[2].substring(6);
        line[3] = line[3].substring(8);
        line[4] = line[4].substring(5);

        Account account = new Account();
        account.setId(line[0]);
        account.setHolder(line[1]);
        try {
            account.setDate(account.simpleDateFormat.parse(line[2]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        account.setAmount(Double.valueOf(line[3]));
        account.setPin(Integer.valueOf(line[4]));
        return account;
    }

}
