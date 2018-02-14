/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.identity.carbon.user.consent.mgt.backend.dbconnect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.base.IdentityRuntimeException;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Class that creates the datasource to get the database connections.
 */
public class JDBCPersistenceManager {

    private static Log log = LogFactory.getLog(JDBCPersistenceManager.class);
    private static volatile JDBCPersistenceManager instance;
    private DataSource dataSource;

    private JDBCPersistenceManager() {

        initDataSource();
    }

    /**
     * @return the instance of the class.
     */
    public static JDBCPersistenceManager getInstance() {

        if (instance == null) {
            synchronized (JDBCPersistenceManager.class) {
                if (instance == null) {
                    instance = new JDBCPersistenceManager();
                }
            }
        }
        return instance;
    }

    /**
     * Initialize the datasource to connect to the database.
     */
    private void initDataSource() {

        try {
            String dataSourceName = "jdbc/WSO2ConsentDB";
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(dataSourceName);
        } catch (NamingException e) {
            String errorMsg = "Error when looking up the Identity Data Source.";
            log.error(errorMsg, e);
            throw IdentityRuntimeException.error(errorMsg, e);
        }
    }

    /**
     * @return a connection from the datasource
     */
    public Connection getDBConnection() throws IdentityRuntimeException {

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            String errMsg = "Error when getting a database connection object from the Identity data source.";
            throw IdentityRuntimeException.error(errMsg, e);
        }
    }
}
