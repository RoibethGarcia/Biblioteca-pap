package edu.udelar.pap.controller;

/**
 * Factory para crear y gestionar controladores
 * Evita dependencias circulares entre controladores
 */
public class ControllerFactory {
    
    private static ControllerFactory instance;
    
    private LectorController lectorController;
    private BibliotecarioController bibliotecarioController;
    private DonacionController donacionController;
    private PrestamoController prestamoController;
    private MainController mainController;
    
    private ControllerFactory() {
        // Constructor privado para Singleton
    }
    
    public static ControllerFactory getInstance() {
        if (instance == null) {
            instance = new ControllerFactory();
        }
        return instance;
    }
    
    public LectorController getLectorController() {
        if (lectorController == null) {
            lectorController = new LectorController();
        }
        return lectorController;
    }
    
    public BibliotecarioController getBibliotecarioController() {
        if (bibliotecarioController == null) {
            bibliotecarioController = new BibliotecarioController();
        }
        return bibliotecarioController;
    }
    
    public DonacionController getDonacionController() {
        if (donacionController == null) {
            donacionController = new DonacionController();
        }
        return donacionController;
    }
    
    public PrestamoController getPrestamoController() {
        if (prestamoController == null) {
            prestamoController = new PrestamoController(this);
        }
        return prestamoController;
    }
    
    public MainController getMainController() {
        if (mainController == null) {
            mainController = new MainController(this);
        }
        return mainController;
    }
}
