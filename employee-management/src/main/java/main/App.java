package main;

import controller.MenuController;
import store.DataStore;
import empUtil.FileUtil;

public class App {
    public static void main(String[] args) {
        DataStore store = FileUtil.loadOrCreateStore();
        MenuController.start(store);
        FileUtil.saveStore(store);
    }
}