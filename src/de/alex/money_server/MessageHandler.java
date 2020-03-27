package de.alex.money_server;

import de.alex.money_server.Exception.Banned;
import de.alex.money_server.Exception.WrongUuid;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageHandler {

    public static Boolean Handle(String message,Socket socket) throws IOException {
        if(message.contains("-== Love AV ==-:")){
            Main.blockIp(socket.getInetAddress().getHostAddress());
            System.out.println("Blocked new Retard");
            return true;
        }
        //System.out.println(Main.sockets.get(socket));
        Main.sockets.remove(socket);
        Main.sockets.put(socket,System.currentTimeMillis());
        //System.out.println(Main.sockets.get(socket));
        if(message.startsWith("Auth:")){
            String username = message.split("%")[1];
            String password = message.split("%")[3];
            String uuid = null;
            try {
                uuid = message.split("%")[5];
            }catch (Exception e){
                System.out.println("Client outdated");
                try {
                    schreibeNachricht(socket,"say Outdated?");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
            try {
                if(login(username,password,uuid)){
                    try {
                        schreibeNachricht(socket,"%"+Config.getUsername()+"%%"+Config.getPassword()+"%");
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
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Banned banned) {
                try {
                    schreibeNachricht(socket,"banned");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (WrongUuid wrongUuid) {
                //wrongUuid.printStackTrace();
                try {
                    schreibeNachricht(socket,"wronguuid");
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
    public static Boolean login(String username,String password,String uuid) throws SQLException, WrongUuid, Banned {
//        if(username.equals("Debug") && password.equals("Debug")){
//            return true;
//        }
        //System.out.println("Trying: "+username+":"+password);
        PreparedStatement ps = Msql.con.prepareStatement("SELECT * FROM `geld_db`.`Users` WHERE `Username`=? AND `password`=?");
        ps.setString(1,username);
        ps.setString(2,password);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            if(rs.getString("Username").equals(username) && rs.getString("password").equals(password)){
                if(rs.getString("Banned").equals("false")){
                    if(rs.getString("uuid").equals(uuid)){
                        return true;
                    }else{
                        //wrong uuid
                        if(rs.getString("uuid").equals("very_good_reset")){
                            PreparedStatement ps2 = Msql.con.prepareStatement("UPDATE `Users` SET `uuid`=? WHERE Username=?");
                            ps2.setString(1,uuid);
                            ps2.setString(2,username);
                            ps2.execute();
                            System.out.println("Set "+username+" new hwid");
                            return true;
                        }
                        throw new WrongUuid(0,"DbUuid: "+rs.getString("uuid")+" Send uuid: "+uuid);
                    }
                }else{
                    //banned
                    throw new Banned(0,"Banned");
                }
            }
        }

        return false;
    }
}
