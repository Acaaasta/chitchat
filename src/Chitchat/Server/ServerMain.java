package Chitchat.Server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class ServerMain {

    Vector<ClientHandler> clients;

    public ServerMain() throws SQLException {

        ServerSocket server = null;
        Socket socket = null;
        clients = new Vector<>();

        try {
            AuthService.connect();

            server = new ServerSocket(8189);
            System.out.println("Сервер запущен!");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился!");
                // создаем нового клиента
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }

    }

    // метод для рассылки сообщения всем клиентам
    public void broadCastMsg(ClientHandler from, String msg) {

        for (ClientHandler o : clients) {
            if (!o.checkBlackList(from.getNick())) {
                o.sendMsg(msg);
            }
        }

//        StringBuilder sb = new StringBuilder();
//        sb.append("/clientlist ");
//
//        for (ClientHandler o : clients) {
//            sb.append(o.getNick() + " ");
//        }
//
//        String out = sb.toString();
//        for (ClientHandler o : clients) {
//            o.sendMsg(out);
//        }

//        for (ClientHandler o: clients) {
//            o.sendMsg(msg);
//
//        }
    }

    // подписываем клиента и добавляем его в список клиентов
    public void subscribe(ClientHandler client) {
        clients.add(client);
        broadcastClientList();
    }

    // отписываем клиента и удаляем его из списка клиентов
    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        broadcastClientList();
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void sendPersonalMsg(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nickTo)) {
                o.sendMsg("from " + from.getNick() + ": " + msg);
                from.sendMsg("to " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Клиент с ником " + nickTo + " не найден!");
    }


    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");

        for (ClientHandler o : clients) {
            sb.append(o.getNick() + " ");
        }

        String out = sb.toString();
        for (ClientHandler o : clients) {
            o.sendMsg(out);
        }
    }

}