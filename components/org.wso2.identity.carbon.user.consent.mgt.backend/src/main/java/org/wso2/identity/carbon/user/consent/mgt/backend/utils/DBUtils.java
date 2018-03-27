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

package org.wso2.identity.carbon.user.consent.mgt.backend.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class that provide the close function to the connections, preparedStatements and resultSets.
 */
public class DBUtils {

    private static Log log = LogFactory.getLog(DBUtils.class);

    public static void closeAllConnections(Connection connection, PreparedStatement preparedStatementOne,
                                           PreparedStatement preparedStatementTwo, ResultSet resultSet) {

        closeDbConnection(connection);
        closePreparedStat(preparedStatementOne);
        closePreparedStat(preparedStatementTwo);
        closeResultSet(resultSet);
    }

    public static void closeAllConnections(Connection connection, PreparedStatement preparedStatement, ResultSet
            resultSet) {

        closeDbConnection(connection);
        closePreparedStat(preparedStatement);
        closeResultSet(resultSet);
    }

    public static void closeAllConnections(Connection connection, PreparedStatement preparedStatement) {

        closeDbConnection(connection);
        closePreparedStat(preparedStatement);
    }

    public static void closeAllConnections(PreparedStatement preparedStatement, ResultSet resultSet) {

        closePreparedStat(preparedStatement);
        closeResultSet(resultSet);
    }

    public static void closeAllConnections(PreparedStatement preparedStatement) {

        closePreparedStat(preparedStatement);
    }

    private static void closeDbConnection(Connection connection) {

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Database error. Could not close the db connection. Continue with others. - " + e
                        .getMessage(), e);
            }
        }
    }

    private static void closePreparedStat(PreparedStatement preparedStatement) {

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                log.error("Database error. Could not close the prepared statement. Continue with others. - " + e
                                .getMessage()
                        , e);
            }
        }
    }

    private static void closeResultSet(ResultSet resultSet) {

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("Database error. Could not close the result set. Continue with others. - " + e.getMessage()
                        , e);
            }
        }
    }
}
