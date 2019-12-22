package de.alex.money_server;

import java.io.*;
import java.net.ServerSocket;

public class Main {
    ServerSocket ssocket;
    Boolean quitting = false;
    public static void main(String[] args)throws IOException {
        // write your code here
        Main main = new Main();
        main.ssocket = new ServerSocket();
        while (!main.quitting){
            main.loop();
        }
        System.out.println("Thanks for using Server Manager by Alex");
    }
    public void loop(){
        
    }
    
}
