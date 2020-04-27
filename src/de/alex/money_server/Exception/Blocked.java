package de.alex.money_server.Exception;


public class Blocked extends RuntimeException{
    int id;
    String Error;

    public Blocked(int x,String Error) {
        this.Error = Error;
        id = x;
    }

    public String toString() {
        return "Blocked[" + id + "]"+Error;
    }
}

