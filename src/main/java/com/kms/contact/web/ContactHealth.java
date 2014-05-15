package com.kms.contact.web;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.kms.contact.service.ContactService;

@Component
public class ContactHealth implements HealthIndicator<Map<String, String>> {
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ContactService contactService;

    @Override
    public Map<String, String> health() {
        Map<String, String> health = new LinkedHashMap<>();
        
        if (this.dataSource == null) {
            health.put("DB connection", "RED");
            health.put("Error", "No DataSource");
            return health;
        }
        
        try {
            String dbDriver = jdbcTemplate.execute((Connection conn) -> conn.getMetaData().getDriverName());
            health.put("DB Connection", "GREEN");
            health.put("DB Driver", dbDriver);
            
            boolean hasContact = jdbcTemplate.execute((Statement stm) -> {
                ResultSet rs = stm.executeQuery("select count(*) from CONTACTS");
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            });
            health.put("Contact List", hasContact? "GREEN" : "RED");
        } catch (DataAccessException ex) {
            health.put("Error", ex.getClass().getName() + ": " + ex.getMessage());
        }
        
        return health;
    }

}
