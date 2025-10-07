package edu.udelar.pap.servlet;

import edu.udelar.pap.servlet.handler.LectorRequestHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet refactorizado para manejar requests de lectores
 * Implementa el patrón Delegation para separar responsabilidades
 */
public class LectorServletRefactored extends HttpServlet {
    
    private final LectorRequestHandler requestHandler;
    
    public LectorServletRefactored() {
        this.requestHandler = new LectorRequestHandler();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        requestHandler.handleGetRequest(pathInfo, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        requestHandler.handlePostRequest(pathInfo, request, response);
    }
}



