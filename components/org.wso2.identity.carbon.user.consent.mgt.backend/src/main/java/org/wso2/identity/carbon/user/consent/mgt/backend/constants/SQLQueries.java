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

package org.wso2.identity.carbon.user.consent.mgt.backend.constants;

/**
 * Class contains the SQL queries that were used in the ConsentDaoImpl class.
 */
public class SQLQueries {

    public static final String TRANSACTION_AND_DATA_CONTROLLER_DETAILS_QUERY = "SELECT A.COLLECTION_METHOD,A.SGUID,A" +
            ".PII_PRINCIPAL_ID,A.CONSENT_TIMESTAMP,B.DATA_CONTROLLER_ID,B.ORGANIZATION_NAME,B.CONTACT_NAME,B.STREET," +
            "B.COUNTRY,B.EMAIL,B.PHONE_NUMBER,B.PUBLIC_KEY,B.POLICY_URL FROM user_consent.TRANSACTION_DETAILS AS A," +
            "user_consent.DATA_CONTROLLER AS B WHERE A.PII_PRINCIPAL_ID=? AND A.DATA_CONTROLLER_ID=B.DATA_CONTROLLER" +
            "_ID";

    public static final String SERVICES_DETAILS_BY_USER_QUERY = "SELECT A.SGUID,A.SERVICE_ID,B.SERVICE_DESCRIPTION," +
            "A.PURPOSE_ID,C.PURPOSE,C.PRIMARY_PURPOSE,C.TERMINATION,C.THIRD_PARTY_DIS,C.THIRD_PARTY_ID," +
            "D.THIRD_PARTY_NAME," +
            "E.PII_CAT_ID,\n" +
            "F.PII_CAT,F.SENSITIVITY\n" +
            "FROM user_consent.SERVICE_MAP_CRID AS A,\n" +
            "user_consent.SERVICES AS B,\n" +
            "user_consent.PURPOSES AS C,\n" +
            "user_consent.THIRD_PARTY AS D,\n" +
            "user_consent.PURPOSE_MAP_PII_CAT AS E,\n" +
            "user_consent.PII_CATEGORY AS F\n" +
            "WHERE SGUID=?\n" +
            "AND A.STATUS='Approved'\n" +
            "AND A.SERVICE_ID=B.SERVICE_ID\n" +
            "AND A.PURPOSE_ID=C.PURPOSE_ID\n" +
            "AND C.THIRD_PARTY_ID=D.THIRD_PARTY_ID\n" +
            "AND C.PURPOSE_ID=E.PURPOSE_ID\n" +
            "AND E.PII_CAT_ID=F.PII_CAT_ID;";

    public static final String SERVICE_DETAILS_BY_USER_AND_THIRD_PARTY_QUERY = "SELECT A.SGUID,A.SERVICE_ID,\n" +
            "B.SERVICE_DESCRIPTION,\n" +
            "A.PURPOSE_ID,\n" +
            "C.PURPOSE,C.PRIMARY_PURPOSE,C.TERMINATION,C.THIRD_PARTY_DIS,C.THIRD_PARTY_ID,\n" +
            "D.THIRD_PARTY_NAME,\n" +
            "E.PII_CAT_ID,\n" +
            "F.PII_CAT,F.SENSITIVITY\n" +
            "FROM user_consent.SERVICE_MAP_CRID AS A,\n" +
            "user_consent.SERVICES AS B,\n" +
            "user_consent.PURPOSES AS C,\n" +
            "user_consent.THIRD_PARTY AS D,\n" +
            "user_consent.PURPOSE_MAP_PII_CAT AS E,\n" +
            "user_consent.PII_CATEGORY AS F\n" +
            "WHERE SGUID=?\n" +
            "AND C.THIRD_PARTY_ID=?\n" +
            "AND A.STATUS='Approved'\n" +
            "AND A.SERVICE_ID=B.SERVICE_ID\n" +
            "AND A.PURPOSE_ID=C.PURPOSE_ID\n" +
            "AND C.THIRD_PARTY_ID=D.THIRD_PARTY_ID\n" +
            "AND C.PURPOSE_ID=E.PURPOSE_ID\n" +
            "AND E.PII_CAT_ID=F.PII_CAT_ID;";

    public static final String CONSENT_DETAILS_BY_USER_BY_THIRD_PARTY_QUERY = "SELECT DISTINCT C.PII_PRINCIPAL_ID,A" +
            ".SERVICE_ID,D.SERVICE_DESCRIPTION,B.THIRD_PARTY_ID\n" +
            "FROM SERVICE_MAP_CRID AS A,PURPOSES AS B,TRANSACTION_DETAILS AS C,SERVICES AS D\n" +
            "WHERE A.SGUID=?\n" +
            "AND A.PURPOSE_ID=B.PURPOSE_ID\n" +
            "AND A.SGUID=C.SGUID\n" +
            "AND A.SERVICE_ID=D.SERVICE_ID\n" +
            "AND B.THIRD_PARTY_ID=?;";

    public static final String PURPOSE_ID_BY_USER_BY_SERVICE_BY_THIRD_PARTY_QUERY = "SELECT A.SGUID,A.SERVICE_ID,A" +
            ".PURPOSE_ID,B.THIRD_PARTY_ID\n" +
            "FROM SERVICE_MAP_CRID AS A,PURPOSES AS B\n" +
            "WHERE A.SERVICE_ID=?\n" +
            "AND A.SGUID=?\n" +
            "AND B.THIRD_PARTY_ID=?\n" +
            "AND A.PURPOSE_ID=B.PURPOSE_ID;";

    public static final String SERVICE_BY_USER_BY_SERVICE_ID_QUERY = "SELECT A.SGUID,A.SERVICE_ID,B" +
            ".SERVICE_DESCRIPTION," +
            "A.PURPOSE_ID,A.STATUS\n" +
            "FROM SERVICE_MAP_CRID AS A,SERVICES AS B\n" +
            "WHERE A.SGUID=?\n" +
            "AND A.STATUS=\"Approved\"\n" +
            "AND A.SERVICE_ID=?\n" +
            "AND A.SERVICE_ID=B.SERVICE_ID;";

    public static final String PURPOSE_BY_USER_BY_SERVICE_QUERY = "SELECT A.SGUID,A.SERVICE_ID," +
            "B.SERVICE_DESCRIPTION," +
            "A.PURPOSE_ID,A.STATUS\n" +
            "FROM SERVICE_MAP_CRID AS A,SERVICES AS B\n" +
            "WHERE A.SGUID=?\n" +
            "AND A.STATUS=\"Approved\"\n" +
            "AND A.SERVICE_ID=?\n" +
            "AND A.PURPOSE_ID=?\n" +
            "AND A.SERVICE_ID=B.SERVICE_ID;";

    public static final String SENSITIVE_PERSONAL_INFO_CATEGORY_QUERY = "SELECT C.PII_CAT_ID,C.PII_CAT,A.PURPOSE_ID," +
            "B.PURPOSE,D.SGUID\n" +
            "FROM user_consent.PURPOSE_MAP_PII_CAT AS A," +
            "user_consent.PURPOSES AS B," +
            "user_consent.PII_CATEGORY AS C," +
            "user_consent.SERVICE_MAP_CRID AS D\n" +
            "WHERE A.PURPOSE_ID=B.PURPOSE_ID\n" +
            "AND A.PII_CAT_ID=C.PII_CAT_ID\n" +
            "AND B.PURPOSE_ID=D.PURPOSE_ID\n" +
            "AND D.SGUID=?\n" +
            "AND C.SENSITIVITY=1\n" +
            "GROUP BY A.PII_CAT_ID,B.PURPOSE_ID;";

    public static final String PURPOSE_CATEGORIES_QUERY = "SELECT A.PURPOSE_ID,C.PURPOSE,A.PURPOSE_CAT_ID," +
            "B.PURPOSE_CAT_SHORT_CODE\n" +
            "FROM user_consent.PURPOSE_MAP_PURPOSE_CAT AS A,\n" +
            "user_consent.PURPOSE_CATEGORY AS B,\n" +
            "user_consent.PURPOSES AS C,\n" +
            "user_consent.SERVICE_MAP_CRID AS D\n" +
            "WHERE A.PURPOSE_CAT_ID=B.PURPOSE_CAT_ID\n" +
            "AND A.PURPOSE_ID=C.PURPOSE_ID\n" +
            "AND A.PURPOSE_ID=D.PURPOSE_ID\n" +
            "AND D.SGUID=?\n" +
            "GROUP BY A.PURPOSE_ID,A.PURPOSE_CAT_ID;";

    public static final String PERSONALLY_IDENTIFIABLE_INFO_CAT_UPDATE_QUERY = "UPDATE user_consent.PII_CATEGORY\n" +
            "SET PII_CAT=?, PII_CAT_DESCRIPTION=?, SENSITIVITY=? WHERE PII_CAT_ID=?;";

    public static final String PURPOSE_DETAILS_UPDATE_QUERY = "UPDATE user_consent.PURPOSES\n" +
            "SET PURPOSE=?,PRIMARY_PURPOSE=?,TERMINATION=?,THIRD_PARTY_DIS=?,THIRD_PARTY_ID=? WHERE PURPOSE_ID=?";

    public static final String CONSENT_BY_USER_REVOKE_QUERY = "UPDATE user_consent.SERVICE_MAP_CRID" +
            "SET STATUS='Revoked',CONSENT_TIME=?" +
            "WHERE SGUID=?" +
            "AND SERVICE_ID=?" +
            "AND PURPOSE_ID=?";

    public static final String PURPOSE_CATS_FOR_PURPOSE_CONF_QUERY = "SELECT A.*, B.PURPOSE_CAT_SHORT_CODE\n" +
            "FROM user_consent.PURPOSE_MAP_PURPOSE_CAT AS A,\n" +
            "user_consent.PURPOSE_CATEGORY AS B\n" +
            "WHERE A.PURPOSE_CAT_ID=B.PURPOSE_CAT_ID\n" +
            "AND A.PURPOSE_ID=?;";

    public static final String PERSONALLY_IDENTIFIABLE_CAT_FOR_PURPOSE_CONF_QUERY = "SELECT A.*,B.PII_CAT\n" +
            "FROM PURPOSE_MAP_PII_CAT AS A,PII_CATEGORY AS B\n" +
            "WHERE A.PII_CAT_ID=B.PII_CAT_ID\n" +
            "AND A.PURPOSE_ID=?;";

    public static final String SERVICES_FOR_CONF_QUERY = "SELECT * FROM SERVICES;";

    public static final String PURPOSE_DETAILS_FOR_SERVICE_CONF_QUERY = "SELECT A.* ,B.PURPOSE FROM " +
            "SERVICE_MAP_PURPOSE AS A, PURPOSES AS B WHERE SERVICE_ID=? AND A.PURPOSE_ID=B.PURPOSE_ID;";

    public static final String SERVICES_FOR_USER_VIEW_QUERY = "SELECT DISTINCT A.SERVICE_ID,C.SERVICE_DESCRIPTION," +
            "B.PII_PRINCIPAL_ID,B.SGUID\n" +
            "FROM SERVICE_MAP_CRID AS A,TRANSACTION_DETAILS AS B,SERVICES AS C\n" +
            "WHERE A.SGUID=B.SGUID\n" +
            "AND B.PII_PRINCIPAL_ID=?\n" +
            "AND A.SERVICE_ID=C.SERVICE_ID;";

    public static final String PURPOSE_ID_BY_USER_BY_SERVICE_QUERY = "SELECT A.SGUID,A.SERVICE_ID,A.PURPOSE_ID\n" +
            "FROM SERVICE_MAP_CRID AS A,PURPOSES AS B\n" +
            "WHERE A.SERVICE_ID=?\n" +
            "AND A.SGUID=?\n" +
            "AND A.PURPOSE_ID=B.PURPOSE_ID;";

    public static final String DATA_CONTROLLER_UPDATE_QUERY = "UPDATE DATA_CONTROLLER\n" +
            "SET ORGANIZATION_NAME=?,\n" +
            "CONTACT_NAME=?,\n" +
            "STREET=?,\n" +
            "COUNTRY=?,\n" +
            "EMAIL=?,\n" +
            "PHONE_NUMBER=?,\n" +
            "PUBLIC_KEY=?,\n" +
            "POLICY_URL=?\n" +
            "WHERE DATA_CONTROLLER_ID=?;";

    public static final String PURPOSE_CATEGORY_UPDATE_QUERY = "UPDATE PURPOSE_CATEGORY\n" +
            "SET PURPOSE_CAT_SHORT_CODE=?,PURPOSE_CAT_DESCRIPTION=?\n" +
            "WHERE PURPOSE_CAT_ID=?;";

    public static final String GET_THIRD_PARTY_BY_ID_QUERY = "SELECT * FROM THIRD_PARTY WHERE THIRD_PARTY_ID=?;";

    public static final String UPDATE_THIRD_PARTY_QUERY = "UPDATE THIRD_PARTY SET THIRD_PARTY_NAME=? WHERE THIRD_" +
            "PARTY_ID=?;";

    public static final String ADD_THIRD_PARTY_QUERY = "INSERT INTO THIRD_PARTY(THIRD_PARTY_NAME) VALUES (?);";

    public static final String SELECT_LAST_INSERTED_THIRD_PARTY_QUERY = "SELECT last_insert_id() AS id FROM " +
            "THIRD_PARTY ORDER BY id DESC LIMIT 1;";

    public static final String SELECT_THIRD_PARTIES_QUERY = "SELECT * FROM THIRD_PARTY;";

    public static final String GET_PURPOSE_CATEGORY_BY_ID = "SELECT * FROM PURPOSE_CATEGORY WHERE PURPOSE_CAT_ID=?;";

    public static final String DELETE_PURPOSE_CATEGORY_MAPPINGS_QUERY = "DELETE FROM PURPOSE_MAP_PURPOSE_CAT WHERE" +
            " PURPOSE_CAT_ID=?;";

    public static final String DELETE_PURPOSE_CATEGORY = "DELETE FROM PURPOSE_CATEGORY WHERE PURPOSE_CAT_ID=?;";

    public static final String ADD_PURPOSE_CATEGORY = "INSERT INTO PURPOSE_CATEGORY (PURPOSE_CAT_SHORT_CODE, " +
            "PURPOSE_CAT_DESCRIPTION) VALUES (?,?);";

    public static final String SELECT_LAST_INSERTED_PURPOSE_CAT = "SELECT last_insert_id() AS id FROM PURPOSE_" +
            "CATEGORY ORDER BY id DESC LIMIT 1;";

    public static final String SELECT_PURPOSE_CATEGORIES_QUERY = "SELECT * FROM PURPOSE_CATEGORY;";

    public static final String GET_SERVICES_BY_ID_QUERY = "SELECT * FROM SERVICES WHERE SERVICE_ID=?;";

    public static final String DELETE_SERVICE_MAPPING_TO_PURPOSE_QUERY = "DELETE FROM SERVICE_MAP_PURPOSE WHERE " +
            "SERVICE_ID=?;";

    public static final String DELETE_SERVICE_MAPPING_TO_USER = "DELETE FROM SERVICE_MAP_CRID WHERE SERVICE_ID=?";

    public static final String DELETE_SERVICE = "DELETE FROM SERVICES WHERE SERVICE_ID=?;";

    public static final String UPDATE_SERVICE = "UPDATE user_consent.SERVICES SET SERVICE_DESCRIPTION=? WHERE " +
            "SERVICE_ID=?;";

    public static final String ADD_SERVICE_MAPPINGS_TO_PURPOSE_QUERY = "INSERT INTO SERVICE_MAP_PURPOSE(SERVICE_ID," +
            "PURPOSE_ID) VALUES(?,?);";

    public static final String ADD_SERVICE = "INSERT INTO user_consent.SERVICES(SERVICE_DESCRIPTION) VALUES(?);";

    public static final String SELECT_LAST_INSERTED_SERVICE_QUERY = "SELECT last_insert_id() AS id FROM SERVICES " +
            "ORDER BY id DESC LIMIT 1;";

    public static final String SELECT_PURPOSE_DETAILS_BY_ID_QUERY = "SELECT A.*,B.THIRD_PARTY_NAME FROM PURPOSES " +
            "AS A,THIRD_PARTY AS B WHERE B" +
            ".THIRD_PARTY_ID=A.THIRD_PARTY_ID AND PURPOSE_ID=?;";

    public static final String DELETE_PURPOSE_MAPPINGS_TO_PII_CAT = "DELETE FROM PURPOSE_MAP_PII_CAT WHERE " +
            "PURPOSE_ID=?;";

    public static final String DELETE_PURPOSE_MAPPINGS_TO_PURPOSE_CAT = "DELETE FROM PURPOSE_MAP_PURPOSE_CAT WHERE" +
            " PURPOSE_ID=?;";

    public static final String DELETE_PURPOSE_MAPPINGS_TO_SERVICE = "DELETE FROM SERVICE_MAP_PURPOSE WHERE" +
            " PURPOSE_ID=?;";

    public static final String DELETE_PURPOSE_MAPPINGS_TO_CONSENT = "DELETE FROM SERVICE_MAP_CRID WHERE PURPOSE_ID=?";

    public static final String DELETE_PURPOSE = "DELETE FROM PURPOSES WHERE PURPOSE_ID=?;";

    public static final String ADD_PURPOSE_MAPPING_WITH_PII_CATEGORY = "INSERT INTO PURPOSE_MAP_PII_CAT(PURPOSE_ID," +
            " PII_CAT_ID) VALUES (?,?) ON DUPLICATE KEY UPDATE PII_CAT_ID=?;";

    public static final String ADD_PURPOSE_MAPPING_WITH_PURPOSE_CAT = "INSERT INTO PURPOSE_MAP_PURPOSE_CAT(" +
            "PURPOSE_ID,PURPOSE_CAT_ID) VALUES (?,?) ON DUPLICATE KEY UPDATE PURPOSE_CAT_ID=?;";

    public static final String ADD_PURPOSE = "INSERT INTO user_consent.PURPOSES(PURPOSE,PRIMARY_PURPOSE,TERMINATION," +
            "THIRD_PARTY_DIS,THIRD_PARTY_ID) VALUES(?,?,?,?,?)";

    public static final String SELECT_PURPOSE_ID_QUERY = "SELECT PURPOSE_ID FROM PURPOSES WHERE PURPOSE=?";

    public static final String GET_PURPOSES = "SELECT A.*,B.THIRD_PARTY_NAME FROM user_consent.PURPOSES AS A," +
            "user_consent.THIRD_PARTY AS B WHERE A.THIRD_PARTY_ID=B.THIRD_PARTY_ID;";

    public static final String GET_PII_CAT_BY_ID = "SELECT * FROM PII_CATEGORY WHERE PII_CAT_ID=?;";

    public static final String DELETE_PII_MAPPINGS_TO_PURPOSE = "DELETE FROM PURPOSE_MAP_PII_CAT WHERE PII_CAT_ID=?;";

    public static final String DELETE_PII_CAT = "DELETE FROM PII_CATEGORY WHERE PII_CAT_ID=?;";

    public static final String SELECT_PII_CATEGORIES = "SELECT * FROM user_consent.PII_CATEGORY;";

    public static final String ADD_PII_CATEGORY = "INSERT INTO user_consent.PII_CATEGORY(PII_CAT,PII_CAT_" +
            "DESCRIPTION,SENSITIVITY) VALUES(?,?,?);";

    public static final String GET_LAST_INSERTED_PII_CAT_ID = "SELECT last_insert_id() as id FROM PII_CATEGORY" +
            " LIMIT 1;";

    public static final String SELECT_DATA_CONTROLLER_BY_ID_QUERY = "SELECT * FROM DATA_CONTROLLER WHERE " +
            "DATA_CONTROLLER_ID=?;";

    public static final String DELETE_DATA_CONTROLLER_MAPPINGS_TO_USER = "DELETE FROM TRANSACTION_DETAILS WHERE " +
            "DATA_CONTROLLER_ID=?;";

    public static final String DELETE_DATA_CONTROLLER = "DELETE FROM DATA_CONTROLLER WHERE DATA_CONTROLLER_ID=?;";

    public static final String ADD_DATA_CONTROLLER = "INSERT INTO user_consent.DATA_CONTROLLER(ORGANIZATION_NAME," +
            "CONTACT_NAME,STREET,COUNTRY,EMAIL,PHONE_NUMBER,PUBLIC_KEY,POLICY_URL) VALUES(?,?,?,?,?,?,?,?)";

    public static final String GET_LAST_INSERTED_DATA_CONTROLLER_ID = "SELECT last_insert_id() AS id FROM " +
            "DATA_CONTROLLER ORDER BY id DESC LIMIT 1;";

    public static final String SELECT_DATA_CONTROLLERS = "SELECT * FROM DATA_CONTROLLER;";

    public static final String SELECT_DATA_CONTROLLER_BY_ID = "SELECT * FROM DATA_CONTROLLER WHERE " +
            "DATA_CONTROLLER_ID=?;";

    public static final String GET_PURPOSE_ID = "SELECT PURPOSE_ID FROM user_consent.PURPOSES WHERE PURPOSE=?";

    public static final String GET_SERVICE_ID = "SELECT SERVICE_ID FROM user_consent.SERVICES WHERE SERVICE_" +
            "DESCRIPTION=?";

    public static final String GET_DATA_CONTROLLER_ID = "SELECT DATA_CONTROLLER_ID FROM user_consent.DATA_CONTROLLER" +
            " WHERE ORGANIZATION_NAME=?;";

    public static final String IS_PII_PRINCIPAL_EXISTS = "SELECT COUNT(PII_PRINCIPAL_ID) as count FROM user_consent." +
            "TRANSACTION_DETAILS WHERE PII_PRINCIPAL_ID=?";

    public static final String GET_PURPOSE_BY_ID = "SELECT A.*,B.THIRD_PARTY_NAME FROM PURPOSES AS A,THIRD_PARTY" +
            " AS B WHERE PURPOSE_ID=? AND A.THIRD_PARTY_ID=B.THIRD_PARTY_ID;";

    public static final String GET_TERMINATION_BY_PURPOSE = "SELECT TERMINATION FROM user_consent.PURPOSES WHERE" +
            " PURPOSE_ID=?";

    public static final String ADD_USER_CONSENTS = "INSERT INTO SERVICE_MAP_CRID (SGUID,SERVICE_ID,PURPOSE_ID," +
            "EXACT_TERMINATION,CONSENT_TIME,COLLECTION_METHOD,STATUS,CONSENT_TYPE) VALUES (?,?,?,?,?,?,?,?);";

    public static final String ADD_USER_DETAILS = "INSERT INTO TRANSACTION_DETAILS VALUES (?,?,?,?,?);";

    public static final String GET_INTERNAL_ID_BY_USER = "SELECT A.SGUID,A.PII_PRINCIPAL_ID FROM TRANSACTION_DETAILS" +
            " AS A WHERE A.PII_PRINCIPAL_ID=?;";
}
