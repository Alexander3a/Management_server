package de.alex.money_server.Exception;

public class Banned extends Exception{
    int id;
    String Error;

    public Banned(int x,String Error) {
        this.Error = Error;
        id = x;
    }

    public String toString() {
        return "Banned[" + id + "]"+Error;
    }
}
