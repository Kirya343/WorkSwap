package org.workswap.datasource.logging;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.internal.DataSourceConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class CustomHibernateConnectionLogger extends DataSourceConnectionProvider {

    private boolean logged;

    public CustomHibernateConnectionLogger() {
        log.debug("[CENTRAL-DB] Custom Connection Logger initialized successfully.");
    }

    @Override
    public Connection getConnection() throws SQLException {
        long startTime = System.currentTimeMillis();
        
        log.debug("[CENTRAL-DB] Step 1: Requesting a database connection from the pool...");

        Connection connection = super.getConnection();
        
        long duration = System.currentTimeMillis() - startTime;
        log.debug("[CENTRAL-DB] Step 2: Connection acquired successfully. Schema: {} (Duration: {}ms)", 
                connection.getSchema(), duration);

        if (!logged) {

            log.info(
                    "[{}] Connected successfully on thread: {}. (Duration: {}ms)",
                    connection.getCatalog(),
                    Thread.currentThread().getName(),
                    duration
            );

            logged = true;
        }

        
        return connection;
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        log.debug("[CENTRAL-DB] Step 3: Returning database connection back to the pool.");
        super.closeConnection(conn);
    }
}
