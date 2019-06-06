/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;


import com.mycompany.common.DBUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import javax.sql.DataSource;
import org.apache.derby.jdbc.ClientDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author roman
 */
public class Main {

    private static DataSource dataSource = null;
    final static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static DataSource prepareDataSource() throws FileNotFoundException{
        
        if (dataSource == null) {
            Properties p = new Properties();
            
            try{
                p.load(new FileInputStream("src/main/resources/com/mycompany/bookrentaldesktop/config.properties"));
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            ClientDataSource ds = new ClientDataSource();
            ds.setDatabaseName(p.getProperty("databasename"));
            ds.setServerName(p.getProperty("serverName"));
            ds.setPassword(p.getProperty("password"));
            ds.setUser(p.getProperty("user"));
            ds.setPortNumber(Integer.parseInt(p.getProperty("portNumber")));
            //ds.setCreateDatabase("create");
            dataSource = ds;
        }
        /*try {
            DBUtils.executeSqlScript(dataSource, BookManager.class.getResource("createTables.sql"));
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }*/
          
        return dataSource;
    }
    
    
    public static void main(String[] args) throws SQLException {

        LOG.info("zaciname");
        BookManagerImpl bookManager = new BookManagerImpl();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
    
    
}
