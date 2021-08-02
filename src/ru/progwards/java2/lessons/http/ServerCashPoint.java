package progwards.java2.lessons.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class ServerCashPoint {
    public static void main(String[] args) {
        try (ServerSocket s = new ServerSocket(8189)){
            while (true) {
                Socket incoming = s.accept();
                StoreService storeService = new StoreServiceImpl();
                AccountServiceImpl accService = new AccountServiceImpl(storeService);
                new Thread(new RequestHandler(incoming, accService)).start();
            }

        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    static class RequestHandler implements Runnable {
        private Socket server;
        private AccountServiceImpl accService;
        private String parameterValues;
        private Set<String> setMethods;

        public RequestHandler(Socket server, AccountServiceImpl accService) {
            this.server = server;
            this.accService = accService;
            setMethods = new HashSet<>(Set.of("balance", "deposit", "withdraw", "transfer"));
        }

        // метод разбирает входящую строку от клиента
        private String parsingString(String str) {
            str = str.replace(" ", "");
            str = str.substring(0, str.length() - "HTTP/1.1".length());
            String[] arr1 = str.split("/");
            String[] arr2 = arr1[1].split("\\?");
            if (setMethods.contains(arr2[0])) {
                parameterValues = arr2[1];
                return requestHandler(arr2[0]);
            }
            return null;
        }

        // в зависимоти от значения строки str вызываем тот или иной метод
        private String requestHandler(String str) {
            switch (str) {
                case "balance":
                    return requestBalance();
                case "deposit":
                    return requestDeposit();
                case "withdraw":
                    return requestWithdraw();
                default:
                    return requestTransfer();
            }
        }

        // метод разбирает строку и подставляет значения в метод balance и возвращает результат в виде строки
        private String requestBalance() {
            String[] arr = parameterValues.split("=");
            String account = arr[1];
            double result = accService.balance(accService.getService().get(arr[1]));
            return "Account " + account + " balance - " + result;
        }

        // метод разбирает строку и подставляет значения в метод deposit и возвращает результат в виде строки
        private String requestDeposit() {
            String[] parameters = parameterValues.split("&");
            String account = parameters[0].split("=")[1];
            String amount = parameters[1].split("=")[1];
            accService.deposit(accService.getService().get(account), Double.parseDouble(amount));
            String result = "Account " + account + " balance top-up in the amount of " + amount + " was successful";
            return result;
        }

        // метод разбирает строку и подставляет значения в метод withdraw и возвращает результат в виде строки
        private String requestWithdraw() {
            String[] parameters = parameterValues.split("&");
            String account = parameters[0].split("=")[1];
            String amount = parameters[1].split("=")[1];
            accService.withdraw(accService.getService().get(account), Double.parseDouble(amount));
            String result = "Withdrawal of funds in the amount of " + amount + " account " +
                    account + " completed successfully";
            return result;
        }

        // метод разбирает строку и подставляет значения в метод transfer и возвращает результат в виде строки
        private String requestTransfer() {
            String[] parameters = parameterValues.split("&");
            String account1 = parameters[0].split("=")[1];
            String account2 = parameters[1].split("=")[1];
            String amount = parameters[2].split("=")[1];
            accService.transfer(accService.getService().get(account1), accService.getService().get(account2),
                    Double.parseDouble(amount));
            String result = "Transfer of funds in the amount of " + amount + " from account " + account1 +
                    " to account " + account2 + " completed successfully";
            return result;
        }

        // метод построчно выводит ответ от сервера
        public static void printToServer(String str, String delim, PrintWriter pw) {
            StringTokenizer stringTokenizer = new StringTokenizer(str, delim);
            while (stringTokenizer.hasMoreTokens())
                pw.println(stringTokenizer.nextToken());
        }

        @Override
        public void run() {
            int count = 0;
            String parsedString = "";
            String answerRequest = "HTTP/1.1 200 OK\n" + "Content-Type: text/html; charset=utf-8\n" + "Content-Length: 1234";
            try(InputStream is = server.getInputStream(); OutputStream os = server.getOutputStream()) {
                Scanner scanner = new Scanner(is);
                PrintWriter pw = new PrintWriter(os);
                while (scanner.hasNextLine()) {
                    String str = scanner.nextLine();

                    // если клиент передал EXIT, то прерываем соединение клиента с сервером
                    if (str.toLowerCase().contains("EXIT".toLowerCase())) {
                        server.shutdownInput();
                        break;
                    }

                    // если переданная строка от клиента удовлетворяет условию, то ожидаем следующую строку
                    if ((str.contains("GET")) && (str.contains("HTTP/1.1"))) {
                        parsedString = str;
                        ++count;
                        continue;
                    }

                    if ((str.contains("hostname")) && !parsedString.isEmpty()) {
                        ++count;
                        continue;
                    }

                    if (count == 2 && str.isEmpty()) {
                        count = 0;
                        // выводим на консоль сообщение от сервера построчно
                        printToServer(answerRequest, "\n", pw);
                        pw.println("");
                        // выводим на консоль результат метода
                        pw.println(parsingString(parsedString));
                        pw.flush();
                    } else {
                        // если сообщение от клиента не соответствует запросу
                        count = 0;
                        pw.println("Bad request");
                        pw.flush();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
