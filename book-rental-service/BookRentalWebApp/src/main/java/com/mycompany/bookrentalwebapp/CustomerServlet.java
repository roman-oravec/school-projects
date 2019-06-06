/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrentalwebapp;

import com.mycompany.bookrental.Customer;
import com.mycompany.bookrental.CustomerManagerImpl;
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
@WebServlet(CustomerServlet.URL_MAPPING + "/*")
public class CustomerServlet extends HttpServlet {
    
    private static final String LIST_JSP = "/customersList.jsp";
    public static final String URL_MAPPING = "/customers";
    
    private final static Logger LOG = LoggerFactory.getLogger(CustomerServlet.class);

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
        showCustomersList(request, response);
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
            case "/add":
                //getting POST parameters from form
                String name = request.getParameter("name");
                String email = request.getParameter("email");   
                String membershipPaid = request.getParameter("membershipPaid");
                //form data validity check
                if (name == null || name.length() == 0 || email == null || email.length() == 0 ) {
                    request.setAttribute("error", "You need to enter all values!");
                    LOG.debug("form data invalid");
                    showCustomersList(request, response);
                    return;
                }
                //form data processing - storing to database
                try {
                    Customer customer = new Customer();
                    customer.setName(name);
                    customer.setEmail(email);
                    customer.setMembership(Boolean.parseBoolean(membershipPaid));
                    getCustomerManager().addCustomer(customer);
                    //redirect-after-POST protects from multiple submission
                    LOG.debug("redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    LOG.error("Cannot add customer", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getCustomerManager().deleteCustomer(getCustomerManager().getCustomerById(id));
                    LOG.debug("redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    LOG.error("Cannot delete customer", e);
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

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    private CustomerManagerImpl getCustomerManager() {
        return (CustomerManagerImpl) getServletContext().getAttribute("customerManager");
    }

    private void showCustomersList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            LOG.debug("showing table of customers");
            request.setAttribute("customers", getCustomerManager().getAllCustomers());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);

    }
}
