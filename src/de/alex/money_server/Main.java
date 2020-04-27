package de.alex.money_server;

import de.alex.money_server.Exception.Blocked;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    static ServerSocket ssocket;
    static HashMap<Socket,Long> sockets = new HashMap<Socket, Long>();
    static HashMap<Socket,Boolean> sockets_threaded = new HashMap<Socket, Boolean>();
    static Boolean quitting = false;
    static Thread openloop;
    static Thread loop;
    static Thread openclassloop = new openclassloop();
    static Thread timeloop;
    static HashMap<Socket,Long> s_t_id = new HashMap<Socket, Long>();
    static Alive alive;
    static ArrayList<Socket> to_kill = new ArrayList<Socket>();
    static ArrayList<String> Blocked_ips = new ArrayList<String>();
    public static void main(String[] args) throws SQLException {
        // write your code here
        if(HWID.bytesToHex(HWID.generateHWID()).equals(hwids.HOME.getUrl())){
            alive = new Alive();
        }

        Main main = new Main();
        Msql.connect();
        try {
            main.ssocket = new ServerSocket(42069);
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
        setTimeloop();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                timeloop.run();
            }
        },1000,1000);
        System.out.println("loop started");
        System.out.println("Thanks for using Server Manager by Alex");
        downloadips();
    }
    public static void downloadips(){
        String result = "";
        try {
            result = Flag_manager.get_flag(Flags.S_Blocked_ip);
        }catch (Exception e){
            System.out.println("Error downloading blocked ips:1");
        }
        try {
            if(!result.contains(",")){
                System.out.println("No blocked ip to download");
                return;
            }
            String[] split = result.split(",");
            for (String current : split) {
                if (!current.equals("")) {
                    Blocked_ips.add(current);
                    System.out.println("Added "+current);
                }
            }
        }catch (Exception e){
            System.out.println("Error downloading blocked ips:2");
        }
    }
    public static void blockIp(String ip){
        if(ip == null){
            throw new RuntimeException();
        }
        if(ip.equalsIgnoreCase("")){
            throw new RuntimeException();
        }
        String orig = Flag_manager.get_flag(Flags.S_Blocked_ip);
        //System.out.println("Org:"+orig);
        String neww = orig+ip+",";
        //System.out.println("new:"+neww);
        Flag_manager.set_flag(Flags.S_Blocked_ip,neww);
        downloadips();
    }
    public static void openloop(){
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
                try {
//                    ssocket = new ServerSocket(11113);
                    Socket socket = ssocket.accept();
                    String ip = socket.getInetAddress().getHostAddress();
                    if(Blocked_ips.contains(ip)){
                        socket.sendUrgentData(69420);
                        socket.close();
                        System.out.println("Refusing blocked Connection");
                        return;
                    }
                    //socket.setSoTimeout(5000);
                    //System.out.println("con");
                    sockets.put(socket,System.currentTimeMillis());
                    sockets_threaded.put(socket,false);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    System.out.println("["+formatter.format(date)+"] New client added on "+socket.getPort()+" and "+socket.getRemoteSocketAddress().toString()+" " + socket.getInetAddress().getHostAddress());
                }catch (IOException e){
                    e.printStackTrace();
                }
//            }
//        },0);
    }
    public static void setTimeloop(){
        Main.timeloop = new Thread(new Runnable() {
            @Override
            public void run() {
                final Iterator it = sockets.entrySet().iterator();
                while (it.hasNext()){
                    Map.Entry<Socket,Long> map = (Map.Entry<Socket, Long>) it.next();
                    if(map.getValue() < (System.currentTimeMillis()-1000)){
                        try {
                            map.getKey().shutdownInput();
                            map.getKey().shutdownOutput();
//                            map.getKey().getChannel().close();
                            map.getKey().close();
                            //System.out.println(map.getKey().isConnected());
                            System.out.println("Closed socket "+map.getKey().toString()+" with timeout time of "+(map.getValue()-(System.currentTimeMillis())));
                            sockets.remove(map.getKey());
                            Long t_id = s_t_id.get(map.getKey());
                            Thread the_one = null;
                            for (Thread t: Main.s_threads
                                 ) {
                                if(t.getId() == t_id){
                                    the_one = t;
                                    System.out.println("Found the thread");
                                }
                            }
//                            if(the_one != null){
//                                the_one.suspend();
//                                System.out.println("kill the thread");
//                            }
                            to_kill.add(map.getKey());
                            s_threads.remove(the_one);
                            s_t_id.remove(map.getKey());
                            sockets_threaded.remove(map.getKey());
                            System.out.println("Murdered the rest");
                            sockets.remove(map.getKey());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    static Boolean said = false;
    static int index = 0;
    static ArrayList<Thread> s_threads = new ArrayList<Thread>();
    public static void loop() throws IOException{
        //System.out.println(sockets.size());
        update_ttime();

        if(sockets.size() == 0){
            String system = "f off retard";
            system += "f";
            return;
        }
        final Set<Socket> socksss = sockets_threaded.keySet();
        //final ArrayList<Thread> s_threadss = s_threads;
        for(int i = 0;i< sockets_threaded.size()  ;i++){
            //System.out.println("Try threading");
            final Socket socket = (Socket) socksss.toArray()[i];
            //System.out.println("Trying");
            final int f_index = index;
            final Thread s_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Boolean quit = false;
                    while (!quit){
                        if(Main.to_kill.contains(socket)){
                            quit = true;
                            to_kill.remove(socket);
                            return;
                        }
                        if(HWID.bytesToHex(HWID.generateHWID()).equals(hwids.HOME.getUrl())){
                            alive.setTime(System.currentTimeMillis());
                        }

                        //System.out.println("Hey from Thread "+index);
                        try {
                            //System.out.println("Trying reading");
                            BufferedReader bufferedReader = null;
                            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            char[] buffer = new char[200];
                            int anzahlZeichen = 0; // blockiert bis Nachricht empfangen
                            anzahlZeichen = bufferedReader.read(buffer, 0, 200);
                            String nachricht = new String(buffer, 0, anzahlZeichen);
                            System.out.println("Thread-"+f_index+"   "+ nachricht);
                            try {
                                MessageHandler.Handle(nachricht,socket);
                            }catch (Blocked e){
                                sockets.remove(socket);
                                quit=true;
                            }
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
            s_threads.add(s_thread);
            //index = s_threads.indexOf(s_thread);
            s_t_id.put(socket,s_thread.getId());
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
                //while (!quitting){
                        //System.out.println("Looped");
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                    if(!quitting){
                                        new Timer().schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                try {
                                                    //update_ttime();
                                                    loop();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },0);
                                    }
                            }
                        },100,100);
                //}
            }
        });
    }
    static int Threads = 0;
    final static Long startup = System.currentTimeMillis();
    static Long timesince = System.currentTimeMillis();
    public static void update_ttime(){
        Threads++;
        Long time =((System.currentTimeMillis()-startup)/1000);
        Double z1 = Double.valueOf(time);
        Double z2 = (double) Threads;
        Double divbyt = z2/z1;
        if(HWID.bytesToHex(HWID.generateHWID()).equals(hwids.HOME.getUrl())){
            alive.setTtime(divbyt*100);
            alive.setSince((System.currentTimeMillis()-timesince));
        }

        //System.out.println(Threads+" since "+(System.currentTimeMillis()-timesince));
        timesince = System.currentTimeMillis();
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
