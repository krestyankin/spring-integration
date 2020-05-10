package ru.krestyankin.autoservice.service;

import ru.krestyankin.autoservice.models.SparePart;

public interface PartService {
    void book(SparePart part) throws Exception;
}
