package de.alex.money_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    ServerSocket ssocket;
    HashMap<Socket,Long> sockets = new HashMap<Socket, Long>();
    Boolean quitting = false;
    Thread openloop;
    Thread loop;
    public static void main(String[] args){
        // write your code here
        Main main = new Main();
        main.initopenloop();
        System.out.println("openloop init done");
        main.openloop.run();
        System.out.println("openloop started");
        main.initloop();
        main.loop.run();
        System.out.println("loop started");
        System.out.println("Thanks for using Server Manager by Alex");
    }
    public void openloop(){
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
                try {
                    ssocket = new ServerSocket(11113);
                    Socket socket = ssocket.accept();
                    System.out.println("con");
                    sockets.put(socket,System.currentTimeMillis());
                }catch (IOException e){
                    e.printStackTrace();
                }
//            }
//        },0);
    }
    public void loop() throws IOException{
        System.out.println(sockets.size());
        if(sockets.size() == 0){
            System.out.println("No active sockets");
        }
        for(int i = 0;i< sockets.size()  ;i++){
            System.out.println("Trying");
            Socket[] hmm = (Socket[]) sockets.keySet().toArray();
            Socket socket = hmm[i];
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            char[] buffer = new char[200];
            int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
            String nachricht = new String(buffer, 0, anzahlZeichen);
            System.out.println(nachricht);
        }

    }
    java.net.Socket warteAufAnmeldung(java.net.ServerSocket serverSocket) throws IOException {
        java.net.Socket socket = serverSocket.accept(); // blockiert, bis sich ein Client angemeldet hat
        return socket;
    }
    public void demo()throws IOException{
        System.out.println("server is started");
        ServerSocket serverSocket= new ServerSocket(11113);
        System.out.println("server is waiting");
        Socket socket=serverSocket.accept();
        System.out.println("Client connected");
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String str=reader.readLine();
        System.out.println("Client data: "+str);
        socket.close();
        serverSocket.close();
    }
    public void initopenloop(){
        openloop = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!quitting){
                    try {
                        openloop();
                        ssocket.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void initloop(){
        loop = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hmm");
                while (!quitting){
                    try {
                        loop();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
