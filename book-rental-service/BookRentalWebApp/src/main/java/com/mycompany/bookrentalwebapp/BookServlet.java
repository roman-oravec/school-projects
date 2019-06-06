/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrentalwebapp;

import com.mycompany.bookrental.Book;
import com.mycompany.bookrental.BookManagerImpl;
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
@WebServlet(BookServlet.URL_MAPPING + "/*")
public class BookServlet extends HttpServlet {
    
    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/books";
    
    private final static Logger LOG = LoggerFactory.getLogger(BookServlet.class);

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
        showBooksList(request, response);
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
                String author = request.getParameter("author");
                String pages = request.getParameter("pages");
                String copies = request.getParameter("copies");
                //form data validity check
                if (name == null || name.length() == 0 || author == null || author.length() == 0 ||
                        pages == null || "0".equals(pages) || "0".equals(copies) || copies == null) {
                    request.setAttribute("error", "You need to enter all values!");
                    LOG.debug("form data invalid");
                    showBooksList(request, response);
                    return;
                }
                //form data processing - storing to database
                try {
                    Book book = new Book();
                    book.setAuthor(author);
                    book.setName(name);
                    book.setPages(Integer.parseInt(pages));
                    book.setCopies(Integer.parseInt(copies));
                    getBookManager().addBook(book);
                    //redirect-after-POST protects from multiple submission
                    LOG.debug("redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    LOG.error("Cannot add book", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getBookManager().deleteBook(getBookManager().getBookById(id));
                    LOG.debug("redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    LOG.error("Cannot delete book", e);
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
    
    private BookManagerImpl getBookManager() {
        return (BookManagerImpl) getServletContext().getAttribute("bookManager");
    }

    private void showBooksList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            LOG.debug("showing table of books");
            request.setAttribute("books", getBookManager().getAllBooks());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);

    }
}
