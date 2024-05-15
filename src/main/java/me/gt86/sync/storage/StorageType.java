package me.gt86.sync.storage;


import java.util.HashMap;
import java.util.Map;

public enum StorageType {

    POKEMON_STORAGE("pokemon_storage"),
    POKEMON_STORAGE_PC("pokemon_storage_pc");

    String name;
    StorageType(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    private static Map<Class, StorageType> registerClass = new HashMap<>();

    public static StorageType getType(Class clazz) {
        return registerClass.get(clazz);
    }

    public static void registerClass(Class clazz, StorageType type) {
        registerClass.put(clazz, type);
    }

}