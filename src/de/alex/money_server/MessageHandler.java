package de.alex.money_server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageHandler {

    public static Boolean Handle(String message,Socket socket){
        //System.out.println(Main.sockets.get(socket));
        Main.sockets.replace(socket,System.currentTimeMillis());
        //System.out.println(Main.sockets.get(socket));
        if(message.startsWith("Auth:")){
            String username = message.split("%")[1];
            String password = message.split("%")[3];
            if(login(username,password)){
                try {
                    schreibeNachricht(socket,"%test%%testplugin%");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    schreibeNachricht(socket,"failed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return true;
    }
    public static void schreibeNachricht(java.net.Socket socket, String nachricht) throws IOException {
        PrintWriter printWriter =
                new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()));
        printWriter.print(nachricht);
        printWriter.flush();
    }
    public static Boolean login(String username,String password){
        if(username.equals("Debug") && password.equals("Debug")){
            return true;
        }
        System.out.println("Trying: "+username+":"+password);
        return false;
    }
}
