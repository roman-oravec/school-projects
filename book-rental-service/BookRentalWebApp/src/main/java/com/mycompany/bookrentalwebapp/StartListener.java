/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrentalwebapp;

import com.mycompany.bookrental.BookManagerImpl;
import com.mycompany.bookrental.CustomerManagerImpl;
import com.mycompany.bookrental.Main;
import com.mycompany.bookrental.RentManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class StartListener implements ServletContextListener {

    private final static Logger LOG = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        LOG.info("webapp initialized");
        ServletContext servletContext = ev.getServletContext();
        DataSource dataSource = Main.prepareDataSource();
        CustomerManagerImpl customerManager = new CustomerManagerImpl();
        customerManager.setDataSource(dataSource);
        BookManagerImpl bookManager = new BookManagerImpl();
        bookManager.setDataSource(dataSource);
        RentManagerImpl rentManager = new RentManagerImpl(dataSource);
        servletContext.setAttribute("customerManager", customerManager);
        servletContext.setAttribute("bookManager", bookManager);
        servletContext.setAttribute("rentManager", rentManager);
        LOG.info("managers created");
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        LOG.info("app ended");
    }
}
