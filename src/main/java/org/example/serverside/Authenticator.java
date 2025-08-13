package org.example.serverside;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

class Authenticator {
    private String filename;
    private Map<String, String> credentials = new HashMap<>();

    Authenticator(String filename){
        this.filename = filename;
        this.loadDetails();
//        this.viewCredentials();
    }

    private void loadDetails() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("users.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while((line = br.readLine()) != null){
                String[] values = line.split(",");
                this.addCredential(values[0], values[1]);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addCredential(String user, String password){
        this.credentials.put(user, password);
    }

    private void viewCredentials(){
        for(Map.Entry<String,String> pair: this.credentials.entrySet()){
            System.out.println("User : " + pair.getKey() + " | Password : " + pair.getValue());
        }
    }

    boolean checkCredential(String u, String p){
        if(!this.credentials.containsKey(u)){
            return false;
        }
        return this.credentials.get(u).equals(p);
    }
}
