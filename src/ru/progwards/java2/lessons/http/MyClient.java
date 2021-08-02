package progwards.java2.lessons.http;

import java.io.IOException;

public class MyClient {
    public static void main(String[] args) {
        StoreService storeService = new StoreServiceImpl();
        AccountServiceImpl accService = new AccountServiceImpl(storeService);
        ATMClient atmClient = new ATMClient();
        atmClient.balance(accService.getService().get("0"));
        atmClient.deposit(accService.getService().get("0"), 1000000);
        atmClient.withdraw(accService.getService().get("0"), 1000000);
        atmClient.balance(accService.getService().get("0"));
        try {
            atmClient.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
