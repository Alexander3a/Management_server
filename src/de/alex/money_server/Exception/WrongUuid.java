package de.alex.money_server.Exception;

public class WrongUuid extends Exception{
    int id;
    String Error;

    public WrongUuid(int x,String Error) {
        this.Error = Error;
        id = x;
    }

    public String toString() {
        return "WrongUuid[" + id + "]"+Error;
    }
}

