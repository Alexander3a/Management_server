package de.alex.money_server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Alive extends JFrame{
    private JPanel Main_Pannel;
    private JLabel Time;
    private JLabel ttime;
    private JLabel Since;
    private JLabel Freeram;

    public Alive(){
        setVisible(true);
        setSize(120, 120);
        setTitle("Login Window");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(Main_Pannel);
        setLocation(0,0);
        //Database_Selecter.addItem("Alex_Money1");
        //Database_Selecter.addItem("Testbase");
    }

    public void setTime(Long time) {
        Time.setText(time.toString());
    }
    public void setTtime(Double ttime){
        Double foff = (Math.round(ttime)/100.0);
        this.ttime.setText((foff)+" per sec");
    }
    public void setSince(Long time){
        Since.setText(time+"");
    }
    public void setFree(String free){
        Freeram.setText(free);
    }
}
