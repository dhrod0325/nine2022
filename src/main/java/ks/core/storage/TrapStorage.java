package ks.core.storage;


public interface TrapStorage {
    String getString(String name);

    int getInt(String name);

    boolean getBoolean(String name);
}
