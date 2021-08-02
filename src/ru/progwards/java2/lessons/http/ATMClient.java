package progwards.java2.lessons.http;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ATMClient implements AccountService {
    Socket socket;

    public ATMClient() {
        try {
            socket = new Socket("localhost", 8189);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // метод пишет запросы на сервер для каждого из методов
    private void writeRequest(String method, Account account1, Account account2, double amount, PrintWriter pw) {
        switch (method) {
            case "balance":
                pw.println("GET/balance?account=" + account1.getId() + " HTTP/1.1");
                break;
            case "deposit":
                pw.println("GET/deposit?account=" + account1.getId() + "&amount=" + amount + " HTTP/1.1");
                break;
            case "withdraw":
                pw.println("GET/withdraw?account=" + account1.getId() + "&amount=" + amount + " HTTP/1.1");
                break;
            default:
                pw.println("GET/transfer?account=" + account1.getId() + "&account=" + account2.getId() +
                        "&amount=" + amount + " HTTP/1.1");
        }
        pw.println("hostname: localhost");
        pw.println("");
        pw.println("exit");
        pw.flush();
    }

    // метод печатает на консоли ответ от сервера
    private void printMessage(InputStream is) {
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }
    }

    @Override
    public double balance(Account account) {
        try(InputStream is = socket.getInputStream(); OutputStream os = socket.getOutputStream()) {
            PrintWriter pw = new PrintWriter(os);
            writeRequest("balance", account, null, 0, pw);
            printMessage(is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public void deposit(Account account, double amount) {
        try(InputStream is = socket.getInputStream(); OutputStream os = socket.getOutputStream()) {
            PrintWriter pw = new PrintWriter(os);
            writeRequest("deposit", account, null, amount, pw);
            printMessage(is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void withdraw(Account account, double amount) {
        try(InputStream is = socket.getInputStream(); OutputStream os = socket.getOutputStream()) {
            PrintWriter pw = new PrintWriter(os);
            writeRequest("withdraw", account, null, amount, pw);
            printMessage(is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void transfer(Account from, Account to, double amount) {
        try(InputStream is = socket.getInputStream(); OutputStream os = socket.getOutputStream()) {
            PrintWriter pw = new PrintWriter(os);
            writeRequest("transfer", from, to, amount, pw);
            printMessage(is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
