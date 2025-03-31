package com.example.wowcher.db;

import androidx.lifecycle.ViewModel;

import com.example.wowcher.classes.User;

import java.util.function.Consumer;

public interface DBSource {

    void getData( String column, Object comparison, Consumer<?> o);
    void create( Object t);
    void delete(String reference);
    void update(String reference, String column, Object newValue);

}
