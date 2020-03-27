package de.alex.money_server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Flag_manager {
    public static String get_flag(String flag_name){
        try {
            PreparedStatement ps = Msql.con.prepareStatement("SELECT * FROM `flags` WHERE `flag_name`=\""+flag_name+"\"");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                return rs.getString("flag_state");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static void set_flag(String flag_name,String flag_state){
        try {
            PreparedStatement ps = Msql.con.prepareStatement("UPDATE `flags` SET `flag_state`=\""+flag_state+"\" WHERE `flag_name`=\""+flag_name+"\"");
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    public static String get_flag(Flags flag){
        try {
            PreparedStatement ps = Msql.con.prepareStatement("SELECT * FROM `flags` WHERE `flag_name`=\""+flag+"\"");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                return rs.getString("flag_state");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static void set_flag(Flags flag,String flag_state){
        try {
            PreparedStatement ps = Msql.con.prepareStatement("UPDATE `flags` SET `flag_state`=\""+flag_state+"\" WHERE `flag_name`=\""+flag+"\"");
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
