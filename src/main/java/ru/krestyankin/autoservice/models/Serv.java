package ru.krestyankin.autoservice.models;

public class Serv extends OrderItem {
    @Override
    public String toString() {
        return "Serv{" +
                "name=" + name + ", " +
                "price=" + price +", " +
                "status=" + status +
                '}';
    }
}
