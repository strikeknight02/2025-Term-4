package com.example.wowcher.db;

import java.util.function.Consumer;

public interface DBSource {

    void getAllData(Consumer<?> method, String column, Object extras);
    void getData(String column, Object comparison, Consumer<?> method);
    void create(Object t);
    void delete(String reference);
    void update(String reference, String column, Object newValues);

}
