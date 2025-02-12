package org.example.mensajeriacliente.controllers;

public class AppSettings {

    private static boolean darkMode = false;

    public static boolean isDarkMode(){
        return darkMode;
    }
    public static void setDarkMode(boolean darkMode){
        AppSettings.darkMode = darkMode;
    }

}
