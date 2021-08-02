package progwards.java2.lessons.http;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Store {
    private static Map<String, Account> store = new HashMap<>();

    static {
        for (int i = 0; i < 30 ; i++) {
            Account acc = new Account();
            String id = "";
            if (("10" + i).length() == 3) {
                id = "10" + i;
            } else {
                id = "1" + i;
            }
            acc.setId(id);
            acc.setPin(1000+i);
            acc.setHolder("Account_"+i);
            acc.setDate(new Date(System.currentTimeMillis()+365*24*3600*1000));
            acc.setAmount(Math.random()*1_000_000);

            store.put(acc.getId(), acc);
        }
    }

    public static Map<String, Account> getStore(){
        return store;
    }
}
