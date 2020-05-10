package ru.krestyankin.autoservice.service;

import ru.krestyankin.autoservice.models.Serv;

public interface ServService {
    void book(Serv serv) throws Exception;
}
