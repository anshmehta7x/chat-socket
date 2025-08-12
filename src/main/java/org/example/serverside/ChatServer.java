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
    private ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();


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

    private void handleClient(Socket clientSocket){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // auto-flush
            String str = in.readLine();
            if (str.equals(killCommand)){
                ss.close();
            }
            else{
                System.out.println("Received message : " + str);
                out.println("Ack : " + str);
                String authResult = this.authenticateClient(clientSocket,in,out);
                if(!authResult.isEmpty()){
                    clients.put(authResult, out);
                }
            }
        }
        catch (Exception e){
            System.out.println("ERROR");
        }

    }

    String authenticateClient(Socket clientSocket, BufferedReader in, PrintWriter out){
        try{
            out.println("Enter username and password (Space separated)");
            String[] enteredCredentials = in.readLine().split(" ");
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

    void addClient(String username, PrintWriter out){
        this.clients.put(username,out);
    }

    void broadcast(String message, String sender){
        Message msg = new Message(sender,message,false);
        for (Map.Entry<String, PrintWriter> entry: clients.entrySet()){
            if(!entry.getKey().equals(sender)){
                entry.getValue().println(msg.stringify());
            }
        }
    }

    void privateMessage(String message, String sender, String receiver){
        Message msg = new Message(sender,message,true);
        for (Map.Entry<String, PrintWriter> entry: clients.entrySet()){
            if(entry.getKey().equals(receiver)){
                entry.getValue().println(msg.stringify());
            }
        }

    }


}
