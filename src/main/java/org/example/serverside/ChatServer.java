package org.example.serverside;

import org.example.Message;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    int port = 5000;
    final String killCommand = "QUIT";
    Authenticator auth = new Authenticator("users.csv");
    ServerSocket ss;
    private final ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();


    public ChatServer() {
        try {
            ss = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create server socket on port " + this.port, e);
        }
    }

    public ChatServer(int port) {
        this.port = port;
        try {
            ss = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create server socket on port " + this.port, e);
        }
    }

    public void runServer(){
        try {
            System.out.println("Server Starting");
            System.out.println("Server started on port "+ this.port);

            while (true) {
                Socket clientSocket = ss.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
        catch (Exception e){
            System.out.println("Error running server" + e );
        }
    }



    String authenticateClient(Socket clientSocket, BufferedReader in, PrintWriter out){
        try{
            out.println("AUTH");
            String[] enteredCredentials = in.readLine().split(":");
            if (!auth.checkCredential(enteredCredentials[0], enteredCredentials[1])){
                out.println("Invalid Credentials, Disconnecting... ");
                clientSocket.close();
                return "";
            }
            else{
                out.println("Welcome to the chatroom");
                return enteredCredentials[0];
            }
        }
        catch (Exception e){
            // handle
            return "";
        }
    }

    private void handleClient(Socket clientSocket){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // auto-flush
            String username = this.authenticateClient(clientSocket,in,out);
            System.out.println(username + " has joined");
            if (username.isEmpty()){
                return;
            }
            else if(this.clients.containsKey(username)){
                out.println("Can only connect from one client");
                clientSocket.close();
            }
            else{
                this.addClient(username,out);
                String str;
                while ((str = in.readLine()) != null) {
                    if (str.equalsIgnoreCase(killCommand)) {
                        out.println("Disconnecting...");
                        break;
                    }
                    System.out.println("Received message from " + username + ": " + str);
                    this.broadcast(str, username);
                }

                this.removeClient(username);
            }
        }
        catch (Exception e){
            System.out.println("ERROR");
        }

    }

    void addClient(String username, PrintWriter out){
        this.clients.put(username,out);
    }

    void removeClient(String username){
        this.clients.remove(username);
    }

    void broadcast(String message, String sender){
        Message msg = new Message(sender,message);
        for (Map.Entry<String, PrintWriter> entry: clients.entrySet()){
            if(!entry.getKey().equals(sender)){
                entry.getValue().println(msg.stringify());
            }
        }
    }

    void privateMessage(String message, String sender, String receiver){
        Message msg = new Message(sender,receiver,message);
        for (Map.Entry<String, PrintWriter> entry: clients.entrySet()){
            if(entry.getKey().equals(receiver)){
                entry.getValue().println(msg.stringify());
            }
        }

    }


}
