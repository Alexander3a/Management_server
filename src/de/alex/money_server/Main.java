package de.alex.money_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Main {
    static ServerSocket ssocket;
    static HashMap<Socket,Long> sockets = new HashMap<Socket, Long>();
    static HashMap<Socket,Boolean> sockets_threaded = new HashMap<Socket, Boolean>();
    static Boolean quitting = false;
    static Thread openloop;
    static Thread loop;
    static Thread openclassloop = new openclassloop();
    public static void main(String[] args){
        // write your code here
        Main main = new Main();
        try {
            main.ssocket = new ServerSocket(11113);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        main.initopenloop();
//        System.out.println("openloop init done");
//        main.openloop.run();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                while (!quitting){
                    openclassloop.run();
                }
                //System.out.println("openloop started");
            }
        },0);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                initloop();
                loop.run();
            }
        },0);
        System.out.println("loop started");
        System.out.println("Thanks for using Server Manager by Alex");
    }
    public static void openloop(){
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
                try {
//                    ssocket = new ServerSocket(11113);
                    Socket socket = ssocket.accept();
                    //System.out.println("con");
                    sockets.put(socket,System.currentTimeMillis());
                    sockets_threaded.put(socket,false);
                    System.out.println("New client added on "+socket.getPort()+" and "+socket.getInetAddress());
                }catch (IOException e){
                    e.printStackTrace();
                }
//            }
//        },0);
    }
    static Boolean said = false;
    static int index = 0;
    static ArrayList<Thread> s_threads = new ArrayList<Thread>();
    public static void loop() throws IOException{
        //System.out.println(sockets.size());
        if(sockets.size() == 0){
            String system = "f off retard";
            system += "f";
            return;
        }
        Iterator socksss = sockets_threaded.entrySet().iterator();
        //final ArrayList<Thread> s_threadss = s_threads;
        for(int i = 0;i< sockets_threaded.size()  ;i++){
            final Socket socket = ((Map.Entry<Socket,Long>)socksss.next()).getKey();
            //System.out.println("Trying");
            final int f_index = index;
            Thread s_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Boolean quit = false;
                    while (!quit){
                        try {
                            //System.out.println("Trying reading");
                            BufferedReader bufferedReader = null;
                            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            char[] buffer = new char[200];
                            int anzahlZeichen = 0; // blockiert bis Nachricht empfangen
                            anzahlZeichen = bufferedReader.read(buffer, 0, 200);
                            String nachricht = new String(buffer, 0, anzahlZeichen);
                            System.out.println("Thread-"+f_index+" -> "+ nachricht);
                            MessageHandler.Handle(nachricht,socket);
                        }catch (SocketException e){
                            if(e.toString().equals("java.net.SocketException: Connection reset")){
                                if(!said){
                                    System.out.println("Stopped Thread");
                                    //said=true;
                                }
                                sockets.remove(socket);
                                quit=true;
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (StringIndexOutOfBoundsException e){
                            System.out.println("Connection seems to be closed by user");
                            if(!said){
                                System.out.println("Stopped Thread");
                                //said=true;
                            }
                            sockets.remove(socket);
                            quit=true;
                        }
                    }
                }
            });
            sockets_threaded.remove(socket);
            s_threads.add(index,s_thread);
            System.out.println("Started new Thread-"+index);
            index++;
            s_thread.run();
        }

    }

    public static void demo()throws IOException{
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
    public static void initopenloop(){
        openloop = new Thread(new Runnable() {
            @Override
            public void run() {
//                while (!quitting){
//                    try {
//                        openloop();
//                        ssocket.close();
//                    }catch (IOException e){
//                        e.printStackTrace();
//                    }
//                }
            }
        });
    }
    public static void initloop(){
        loop = new Thread(new Runnable() {
            @Override
            public void run() {
                //System.out.println("hmm");
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
    public static class openclassloop extends Thread{
        public void run(){
            //System.out.println("running i guess");
            if(!quitting){
                openloop();
            }
            //System.out.println("ran i guess");
        }
    }
}
