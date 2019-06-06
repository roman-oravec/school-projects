/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrentalwebapp;

import com.mycompany.bookrental.RentManagerImpl;
import com.mycompany.common.ServiceFailureException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author roman
 */
@WebServlet(RentServlet.URL_MAPPING + "/*")
public class RentServlet extends HttpServlet {


    private static final String LIST_JSP = "/rentsList.jsp";
    public static final String URL_MAPPING = "/rents";
    
    private final static Logger LOG = LoggerFactory.getLogger(RentServlet.class);

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOG.debug("GET ...");
        showRentsList(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        LOG.debug("POST ... {}",action);
        switch (action) {
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getRentManager().deleteRent(getRentManager().getRentById(id));
                    LOG.debug("redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    LOG.error("Cannot delete rent", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
                //TODO
                return;
            default:
                LOG.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    
    }

    
    private RentManagerImpl getRentManager() {
        return (RentManagerImpl) getServletContext().getAttribute("rentManager");
    }

    private void showRentsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            LOG.debug("showing table of rents");
            request.setAttribute("rents", getRentManager().getAllRents());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);

    }

}
