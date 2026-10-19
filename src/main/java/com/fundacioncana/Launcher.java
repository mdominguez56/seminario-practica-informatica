package com.fundacioncana;

import javafx.application.Application;

/**
 * Punto de entrada del JAR ejecutable. Se separa de App porque una clase que extiende
 * Application no puede ser la clase principal de un JAR con JavaFX en el classpath.
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
