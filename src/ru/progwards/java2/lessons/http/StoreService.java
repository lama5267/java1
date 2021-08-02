package progwards.java2.lessons.http;
import java.util.Collection;

public interface StoreService {
    Account get(String id);
    Collection<Account> get();
    void delete(String id);
    void insert(Account account);
    void update(Account account);
}
