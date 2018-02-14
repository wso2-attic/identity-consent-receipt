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

package org.wso2.identity.carbon.user.consent.mgt.backend.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.identity.carbon.user.consent.mgt.backend.constants.ConsentReceiptConstants;
import org.wso2.identity.carbon.user.consent.mgt.backend.constants.SQLQueries;
import org.wso2.identity.carbon.user.consent.mgt.backend.dao.MainDao;
import org.wso2.identity.carbon.user.consent.mgt.backend.dbconnect.JDBCPersistenceManager;
import org.wso2.identity.carbon.user.consent.mgt.backend.exception.DataAccessException;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.ConsentDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.DataControllerDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.PiiCategoryDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.PurposeCategoryDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.PurposeDetailsDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.ServicesDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.ThirdPartyDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class contains the implementations of the DAO interfaces which can be used to operate on the consent database.
 */
public class ConsentDaoImpl implements MainDao {

    private static final Log log = LogFactory.getLog(ConsentDaoImpl.class);
    private List<ConsentDO> consents;
    private List<ServicesDO> serviceList;

    public ConsentDaoImpl() {

    }

    /**
     * Consent Receipt Creation Methods.
     * This constructor gets the user and data controller details from the database.
     *
     * @param piiPrincipalId is the id of the user.
     */
    public ConsentDaoImpl(String piiPrincipalId) throws DataAccessException {

        Connection con = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.TRANSACTION_AND_DATA_CONTROLLER_DETAILS_QUERY;
        try {
            ConsentDO consentDO;
            DataControllerDO dataController;
            consents = new ArrayList<>();

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, piiPrincipalId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                dataController = new DataControllerDO(resultSet.getInt(5),
                        resultSet.getString(6), resultSet.getString(7),
                        resultSet.getString(8), resultSet.getString(9),
                        resultSet.getString(10), resultSet.getString(11),
                        resultSet.getString(12), resultSet.getString(13));
                consentDO = new ConsentDO(resultSet.getString(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), dataController);
                consents.add(consentDO);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get the user and data controller details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_AND_DATA_CONTROLLER_FAILURE.
                            getErrorMessage(), ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_AND_DATA_CONTROLLER_FAILURE.
                    getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(con, preparedStatement, resultSet);
        }
    }

    /**
     * Consent Receipt Creation Methods.
     * This constructor gets the services and purposes for one user.
     *
     * @param sguid is the mapping in the internal system to map the user data and the consent data of that user.
     * @param dummy is a dummy value to differentiate the constructors.
     */
    public ConsentDaoImpl(String sguid, boolean dummy) throws DataAccessException {

        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.SERVICES_DETAILS_BY_USER_QUERY;
        try {
            ServicesDO services;
            serviceList = new ArrayList<>();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sguid);
            resultSet = preparedStatement.executeQuery();

            PurposeDetailsDO purpose;
            PiiCategoryDO piiCategory;

            while (resultSet.next()) {
                piiCategory = new PiiCategoryDO(resultSet.getInt("PII_CAT_ID"), resultSet.getString("PII_CAT"),
                        resultSet.getInt("SENSITIVITY"));
                purpose = new PurposeDetailsDO(resultSet.getString(1), resultSet.getInt(4),
                        resultSet.getString(5), resultSet.getString(6), resultSet.getString(7),
                        resultSet.getString(8), resultSet.getInt(9), resultSet.getString(10), piiCategory);
                services = new ServicesDO(resultSet.getInt(2), resultSet.getString(3), purpose);
                serviceList.add(services);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get consent details for the user.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
    }

    /**
     * This constructor gets the services and purposes by third party for one user.
     *
     * @param sguid        is the system generated user id for the user.
     * @param thirdPartyId is the id of the third party.
     */
    public ConsentDaoImpl(String sguid, int thirdPartyId) throws DataAccessException {

        Connection con = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.SERVICE_DETAILS_BY_USER_AND_THIRD_PARTY_QUERY;
        try {
            ServicesDO services;
            PiiCategoryDO piiCategory;
            serviceList = new ArrayList<>();

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, sguid);
            preparedStatement.setInt(2, thirdPartyId);
            resultSet = preparedStatement.executeQuery();

            resultSet.last();
            PurposeDetailsDO[] purposeDetails = new PurposeDetailsDO[resultSet.getRow()];
            resultSet.beforeFirst();

            int i = 0;
            while (resultSet.next()) {
                piiCategory = new PiiCategoryDO(resultSet.getInt("PII_CAT_ID"),
                        resultSet.getString("PII_CAT"), resultSet.getInt("SENSITIVITY"));
                purposeDetails[i] = new PurposeDetailsDO(resultSet.getString(1),
                        resultSet.getInt(4), resultSet.getString(5),
                        resultSet.getString(6), resultSet.getString(7),
                        resultSet.getString(8), resultSet.getInt(9),
                        resultSet.getString(10), piiCategory);
                services = new ServicesDO(resultSet.getInt(2), resultSet.getString(3),
                        purposeDetails[i]);
                serviceList.add(services);
                i++;
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get consent details of the user to third party.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FOR_THIRD_PARTY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FOR_THIRD_PARTY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(con, preparedStatement, resultSet);
        }
    }

    /**
     * This method returns the user details.
     *
     * @return the user details.
     */
    @Override
    public List<ConsentDO> getUserConsent() {

        return consents;
    }

    /**
     * This method returns services.
     *
     * @return the users consents.
     */
    @Override
    public List<ServicesDO> getServices() {

        return serviceList;
    }

    /**
     * @param subjectName  is the user id.
     * @param thirdPartyId is the id of the third party.
     * @return the details of the consent given to the above third party.
     */
    @Override
    public List<ServicesDO> getServiceDetailsByThirdParty(String subjectName, int thirdPartyId)
            throws DataAccessException {

        ConsentDaoImpl consentDaoImpl = new ConsentDaoImpl();
        ConsentDO user = consentDaoImpl.getSGUIDByUser(subjectName);
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.CONSENT_DETAILS_BY_USER_BY_THIRD_PARTY_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getSGUID());
            preparedStatement.setInt(2, thirdPartyId);
            resultSet = preparedStatement.executeQuery();

            List<ServicesDO> servicesDOList = new ArrayList<>();
            while (resultSet.next()) {
                ServicesDO servicesDO = new ServicesDO();
                servicesDO.setServiceId(resultSet.getInt(2));
                servicesDO.setServiceDescription(resultSet.getString(3));
                List<Integer> purposeIdList = getPurposeIdByUserByServiceByThirdParty(connection, user.getSGUID(),
                        resultSet
                                .getInt(2), thirdPartyId);
                List<PurposeDetailsDO> purposeDetailsDOList = new ArrayList<>();
                for (Integer aPurposeIdList : purposeIdList) {
                    PurposeDetailsDO purposeDetailsDO = getPurposeDetailsByPurposeId(connection, aPurposeIdList);
                    purposeDetailsDOList.add(purposeDetailsDO);
                }
                servicesDO.setPurposeDetails(purposeDetailsDOList.toArray(new PurposeDetailsDO[0]));
                servicesDOList.add(servicesDO);
            }
            return servicesDOList;
        } catch (SQLException e) {
            log.error("Database error. Could not get consent details of the user to third party.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FOR_THIRD_PARTY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FOR_THIRD_PARTY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
    }

    private List<Integer> getPurposeIdByUserByServiceByThirdParty(Connection connection, String sguid, int serviceId,
                                                                  int thirdPartyId) throws DataAccessException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.PURPOSE_ID_BY_USER_BY_SERVICE_BY_THIRD_PARTY_QUERY;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setString(2, sguid);
            preparedStatement.setInt(3, thirdPartyId);
            resultSet = preparedStatement.executeQuery();
            List<Integer> purposeIdList = new ArrayList<>();
            while (resultSet.next()) {
                int i = resultSet.getInt(3);
                purposeIdList.add(i);
            }
            return purposeIdList;
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose IDs for services.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_IDS_FOR_SERVICES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_IDS_FOR_SERVICES_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement, resultSet);
        }
    }

    /**
     * This method gets the SGUID for one user.
     *
     * @param piiPrincipalId id of the user.
     * @return the System generated user id for the user name.
     */
    @Override
    public ConsentDO getSGUIDByUser(String piiPrincipalId) throws DataAccessException {

        Connection con = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.GET_INTERNAL_ID_BY_USER;
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, piiPrincipalId);
            resultSet = preparedStatement.executeQuery();

            ConsentDO consentDO = new ConsentDO();
            resultSet.next();
            consentDO.setSGUID(resultSet.getString(1));
            return consentDO;
        } catch (SQLException e) {
            log.error("Database error. Could not get internal ID for the user.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_INTERNAL_ID_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_INTERNAL_ID_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(con, preparedStatement, resultSet);
        }
    }

    /**
     * Consent Adding Methods.
     * This method adds an user's consent details to the database.
     *
     * @param consentDO is the object which contains the user details.
     * @param services  is the array which contains the consent details of the user.
     */
    @Override
    public void addUserConsentDetails(ConsentDO consentDO, ServicesDO[] services) throws DataAccessException {

        Connection con = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        Savepoint savepoint = null;

        String query = SQLQueries.ADD_USER_DETAILS;
        try {
            con.setAutoCommit(false);
            savepoint = con.setSavepoint();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, consentDO.getCollectionMethod());
            preparedStatement.setString(2, consentDO.getSGUID());
            preparedStatement.setString(3, consentDO.getPiiPrincipalId());
            preparedStatement.setString(4, consentDO.getConsentTimestamp());
            preparedStatement.setInt(5, consentDO.getDataController().getDataControllerId());
            preparedStatement.executeUpdate();
            addUserConsentDetails(con, consentDO, services);
            con.commit();
            log.info("Successfully added the user " + consentDO.getPiiPrincipalId() + " to the database");
        } catch (SQLException e) {
            try {
                con.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback the user consent adding.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_CONSENT_ADD_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_CONSENT_ADD_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not add the user consents to the database.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_USER_CONSENT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_USER_CONSENT_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(con, preparedStatement);
        }
    }

    private void addUserConsentDetails(Connection connection, ConsentDO consentDO, ServicesDO[] services) throws
            DataAccessException {

        PreparedStatement preparedStatement = null;

        for (ServicesDO service : services) {
            for (int i = 0; i < service.getPurposeDetailsArr().length; i++) {
                String query = SQLQueries.ADD_USER_CONSENTS;
                try {
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, consentDO.getSGUID());
                    preparedStatement.setInt(2, service.getServiceId());
                    preparedStatement.setInt(3, service.getPurposeDetailsArr()[i].getPurposeId());
                    preparedStatement.setString(4, getPurposeTerminationDays(connection, service
                            .getPurposeDetailsArr()[i].getPurposeId(), service.getPurposeDetailsArr()[i].getTimestamp()));
                    preparedStatement.setString(5, service.getPurposeDetailsArr()[i].getTimestamp());
                    preparedStatement.setString(6, service.getPurposeDetailsArr()[i]
                            .getCollectionMethod());
                    preparedStatement.setString(7, "Approved");
                    preparedStatement.setString(8, service.getPurposeDetailsArr()[i].getConsentType());
                    preparedStatement.executeUpdate();
                    log.info("Successfully added the user consents of the " + consentDO.getPiiPrincipalId()
                            + " to the database");
                } catch (SQLException e) {
                    log.error("Database error. Could not add consents for the user to the database.", e);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_USER_CONSENT_FAILURE.getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_USER_CONSENT_FAILURE.getErrorCode(), e);
                } finally {
                    DBUtils.closeAllConnections(preparedStatement);
                }
            }
        }
    }

    private String getPurposeTerminationDays(Connection connection, int purposeId, String consentedTime) throws
            DataAccessException {

        String exactTermination;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.GET_TERMINATION_BY_PURPOSE;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, purposeId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(consentedTime));
            cal.add(Calendar.DATE, resultSet.getInt(1));
            exactTermination = dateFormat.format(cal.getTime());
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose termination days.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_TERMINATION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_TERMINATION_FAILURE.getErrorCode(), e);
        } catch (ParseException e) {
            log.error("Time error. consented time is not in required date format.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DATE_PARSE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DATE_PARSE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement, resultSet);
        }
        return exactTermination;
    }

    /**
     * This method returns details of the services for an user by service id.
     *
     * @param subjectName is the user name of a user.
     * @param serviceId   is the id of the service.
     * @return the consent details.
     */
    @Override
    public ServicesDO getServiceByUserByServiceId(String subjectName, int serviceId) throws DataAccessException {

        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ConsentDaoImpl consentDaoImpl = new ConsentDaoImpl();
        ConsentDO consentDO = consentDaoImpl.getSGUIDByUser(subjectName);

        String query = SQLQueries.SERVICE_BY_USER_BY_SERVICE_ID_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, consentDO.getSGUID());
            preparedStatement.setInt(2, serviceId);
            resultSet = preparedStatement.executeQuery();

            List<PurposeDetailsDO> purposeDetailsDOList = new ArrayList<>();
            while (resultSet.next()) {
                PurposeDetailsDO purposeDetailsDO = getPurposeDetailsByPurposeId(connection,
                        resultSet.getInt(4));
                purposeDetailsDOList.add(purposeDetailsDO);
            }
            resultSet.first();
            ServicesDO servicesDO = new ServicesDO();
            servicesDO.setServiceId(resultSet.getInt(2));
            servicesDO.setServiceDescription(resultSet.getString(3));
            servicesDO.setPurposeDetails(purposeDetailsDOList.toArray(new PurposeDetailsDO[0]));
            return servicesDO;
        } catch (SQLException e) {
            log.error("Database error. Could not get services for the user.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_CONSENT_SERVICES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_CONSENT_SERVICES_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
    }

    private PurposeDetailsDO getPurposeDetailsByPurposeId(Connection connection, int purposeId) throws
            DataAccessException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.GET_PURPOSE_BY_ID;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, purposeId);
            resultSet = preparedStatement.executeQuery();
            resultSet.first();

            PurposeDetailsDO purposeDetailsDO = new PurposeDetailsDO();
            purposeDetailsDO.setPurposeId(resultSet.getInt(1));
            purposeDetailsDO.setPurpose(resultSet.getString(2));
            purposeDetailsDO.setPrimaryPurpose(String.valueOf(resultSet.getInt(4)));
            purposeDetailsDO.setTermination(String.valueOf(resultSet.getInt(5)));
            purposeDetailsDO.setThirdPartyDis(String.valueOf(resultSet.getInt(6)));
            purposeDetailsDO.setThirdPartyId(resultSet.getInt(7));
            purposeDetailsDO.setthirdPartyName(resultSet.getString(8));

            PurposeCategoryDO[] purposeCategoryDOArr = getPurposeCatsForPurposeConf(connection, resultSet.getInt(1))
                    .toArray
                            (new PurposeCategoryDO[0]);
            purposeDetailsDO.setPurposeCategoryDOArr(purposeCategoryDOArr);

            PiiCategoryDO[] piiCategoryDOArr = getPersonalIdentifyCatForPurposeConf(connection, resultSet.getInt(1))
                    .toArray
                            (new PiiCategoryDO[0]);
            purposeDetailsDO.setpiiCategoryArr(piiCategoryDOArr);
            return purposeDetailsDO;
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement, resultSet);
        }
    }

    /**
     * Get details of a purpose for a user.
     *
     * @param subjectName is the user name.
     * @param serviceId   is the id of the service.
     * @param purposeId   is the id of the purpose.
     * @return the consent details of the relevant user by the purpose.
     */
    @Override
    public PurposeDetailsDO getPurposeByUserByService(String subjectName, int serviceId, int purposeId)
            throws DataAccessException {

        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ConsentDaoImpl consentDaoImpl = new ConsentDaoImpl();
        ConsentDO consentDO = consentDaoImpl.getSGUIDByUser(subjectName);

        String query = SQLQueries.PURPOSE_BY_USER_BY_SERVICE_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, consentDO.getSGUID());
            preparedStatement.setInt(2, serviceId);
            preparedStatement.setInt(3, purposeId);
            resultSet = preparedStatement.executeQuery();
            resultSet.first();
            return getPurposeDetailsByPurposeId(connection, resultSet.getInt(4));
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose details for the user by service.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FOR_USER_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FOR_USER_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
    }

    public ConsentDO getUserNameFromSGUID(String subjectName) throws DataAccessException {

        ConsentDO consentDO = getSGUIDByUser(subjectName);
        consentDO.setPiiPrincipalId(subjectName);
        return consentDO;
    }

    /**
     * This method is checking whether the subject id(piiPrincipalId) is already in the database or not.
     *
     * @param piiPrincipalId user name of the user.
     * @return the count of ids in the database which equals to provided user id.
     */
    @Override
    public int isPiiPrincipalExists(String piiPrincipalId) throws DataAccessException {

        int i;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.IS_PII_PRINCIPAL_EXISTS;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, piiPrincipalId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            i = resultSet.getByte("count");
        } catch (SQLException e) {
            log.error("Database error. Could not get the existence of the user in the database.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_USER_NAME_EXISTS_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_USER_NAME_EXISTS_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
        return i;
    }

    /**
     * This method checks whether the data controller is already in the database or not.
     *
     * @param orgName name of the data controller.
     * @return the data controller id.
     */
    @Override
    public int isDataControllerExists(String orgName) throws DataAccessException {

        int dataControllerId;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.GET_DATA_CONTROLLER_ID;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, orgName);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                dataControllerId = 0;
            } else {
                dataControllerId = resultSet.getInt("DATA_CONTROLLER_ID");
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get the existence of the data controller.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DATA_CONTROLLER_EXISTS_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DATA_CONTROLLER_EXISTS_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
        return dataControllerId;
    }

    /**
     * JSON Parser Methods.
     * This method gets the service id by service name.
     *
     * @param serviceName name of the service.
     * @return the service id of the above service.
     */
    @Override
    public int getServiceIdByService(String serviceName) throws DataAccessException {

        int serviceId;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.GET_SERVICE_ID;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, serviceName);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                serviceId = 0;
            } else {
                serviceId = resultSet.getInt("SERVICE_ID");
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get service ID.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICE_ID_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICE_ID_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
        return serviceId;
    }

    /**
     * JSON Parser Methods
     * This method gets the purpose id by purpose name.
     *
     * @param purposeName name of the purpose.
     * @return the id of the above purpose.
     */
    @Override
    public int getPurposeIdByPurpose(String purposeName) throws DataAccessException {

        int purposeId;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.GET_PURPOSE_ID;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, purposeName);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                purposeId = 0;
            } else {
                purposeId = resultSet.getInt("PURPOSE_ID");
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose ID.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_ID_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_ID_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
        return purposeId;
    }

    /**
     * JSON Parser Methods.
     * This method gets the sensitive personal info category for one user.
     *
     * @param SGUID System Generated User Id for a user.
     * @return the sensitive personal info categories.
     */
    @Override
    public List<PiiCategoryDO> getSensitivePersonalInfoCategory(String SGUID) throws DataAccessException {

        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.SENSITIVE_PERSONAL_INFO_CATEGORY_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, SGUID);
            resultSet = preparedStatement.executeQuery();
            PiiCategoryDO piiCategory;
            List<PiiCategoryDO> piiCategoryList = new ArrayList<>();
            int temp = 0;
            while (resultSet.next()) {
                if (resultSet.getRow() == 0) {
                    temp = resultSet.getInt(1);
                    piiCategory = new PiiCategoryDO();
                    piiCategory.setPiiCatId(temp);
                    piiCategory.setPiiCat(resultSet.getString(2));
                    piiCategoryList.add(piiCategory);
                } else {
                    if (temp != resultSet.getInt(1)) {
                        temp = resultSet.getInt(1);
                        piiCategory = new PiiCategoryDO();
                        piiCategory.setPiiCatId(temp);
                        piiCategory.setPiiCat(resultSet.getString(2));
                        piiCategoryList.add(piiCategory);
                    }
                }
            }
            return piiCategoryList;
        } catch (SQLException e) {
            log.error("Database error. Could not get the sensitive personal info categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SENSITIVE_PII_CATEGORIES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SENSITIVE_PII_CATEGORIES_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
    }

    /**
     * JSON Parser Methods.
     * This method gets purpose categories for one user.
     *
     * @param sguid System generated user id.
     * @return the list of purpose categories for the relevant user.
     */
    @Override
    public List<PurposeDetailsDO> getPurposeCategories(String sguid) throws DataAccessException {

        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.PURPOSE_CATEGORIES_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sguid);
            resultSet = preparedStatement.executeQuery();

            List<PurposeDetailsDO> purposeCategoryDetailsList = new ArrayList<>();
            PurposeDetailsDO purposeCategoryDetails;
            while (resultSet.next()) {
                purposeCategoryDetails = new PurposeDetailsDO();
                purposeCategoryDetails.setPurposeId(resultSet.getInt(1));
                purposeCategoryDetails.setPurpose(resultSet.getString(2));
                purposeCategoryDetails.setPurposeCatId(resultSet.getInt(3));
                purposeCategoryDetails.setPurposeCatShortCode(resultSet.getString(4));
                purposeCategoryDetailsList.add(purposeCategoryDetails);
            }
            return purposeCategoryDetailsList;
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATEGORIES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATEGORIES_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
    }

    /**
     * Data Controller Configuration.
     * Get Data Controller details from the database.
     *
     * @param dataControllerId id of the data controller.
     * @return details of the data controller.
     */
    @Override
    public DataControllerDO getDataController(int dataControllerId) throws DataAccessException {

        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.SELECT_DATA_CONTROLLER_BY_ID;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, dataControllerId);
            resultSet = preparedStatement.executeQuery();
            resultSet.first();
            DataControllerDO dataControllerDO = new DataControllerDO();
            dataControllerDO.setDataControllerId(resultSet.getInt(1));
            dataControllerDO.setOrgName(resultSet.getString(2));
            dataControllerDO.setContactName(resultSet.getString(3));
            dataControllerDO.setStreet(resultSet.getString(4));
            dataControllerDO.setCountry(resultSet.getString(5));
            dataControllerDO.setEmail(resultSet.getString(6));
            dataControllerDO.setPhoneNo(resultSet.getString(7));
            dataControllerDO.setPublicKey(resultSet.getString(8));
            dataControllerDO.setPolicyUrl(resultSet.getString(9));
            return dataControllerDO;
        } catch (SQLException e) {
            log.error("Database error. Could not get the data controller details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_DATA_CONTROLLER_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_DATA_CONTROLLER_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
    }

    /**
     * Data Controller Configuration.
     * Get all the details of the Data Controllers of the database.
     *
     * @return a list of details of the data controllers.
     */
    @Override
    public List<DataControllerDO> getDataControllerList() throws DataAccessException {

        List<DataControllerDO> dataControllerList = new ArrayList<>();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement selectAllPrepStat = null;
        ResultSet resultSet = null;

        String selectAllQuery = SQLQueries.SELECT_DATA_CONTROLLERS;
        try {
            selectAllPrepStat = connection.prepareStatement(selectAllQuery);
            resultSet = selectAllPrepStat.executeQuery();
            while (resultSet.next()) {
                DataControllerDO dataController = new DataControllerDO();
                dataController.setDataControllerId(resultSet.getInt(1));
                dataController.setOrgName(resultSet.getString(2));
                dataController.setContactName(resultSet.getString(3));
                dataController.setStreet(resultSet.getString(4));
                dataController.setCountry(resultSet.getString(5));
                dataController.setEmail(resultSet.getString(6));
                dataController.setPhoneNo(resultSet.getString(7));
                dataController.setPublicKey(resultSet.getString(8));
                dataController.setPolicyUrl(resultSet.getString(9));
                dataControllerList.add(dataController);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get data controllers' details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_DATA_CONTROLLERS_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_DATA_CONTROLLERS_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, selectAllPrepStat, resultSet);
        }
        return dataControllerList;
    }

    /**
     * Add the Data Controller details to the database.
     *
     * @param dataController object which contains the details of a data controller.
     * @return the added details of the data controller.
     */
    @Override
    public DataControllerDO addDataController(DataControllerDO dataController) throws DataAccessException {

        DataControllerDO addedDataControllerDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        PreparedStatement selectIdPrepStat;
        ResultSet resultSet;

        String query = SQLQueries.ADD_DATA_CONTROLLER;
        String selectIdQuery = SQLQueries.GET_LAST_INSERTED_DATA_CONTROLLER_ID;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, dataController.getOrgName());
            preparedStatement.setString(2, dataController.getContactName());
            preparedStatement.setString(3, dataController.getStreet());
            preparedStatement.setString(4, dataController.getCountry());
            preparedStatement.setString(5, dataController.getEmail());
            preparedStatement.setString(6, dataController.getPhoneNo());
            preparedStatement.setString(7, dataController.getPublicKey());
            preparedStatement.setString(8, dataController.getPolicyUrl());
            preparedStatement.executeUpdate();

            selectIdPrepStat = connection.prepareStatement(selectIdQuery);
            resultSet = selectIdPrepStat.executeQuery();
            resultSet.first();
            addedDataControllerDO = getDataController(resultSet.getInt(1));
            log.info("Successfully added the data controller details to the database");
        } catch (SQLException e) {
            log.error("Database error. Could not add data controller details to the database.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_DATA_CONTROLLER_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_DATA_CONTROLLER_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return addedDataControllerDO;
    }

    /**
     * Data Controller Configuration.
     * Update data Controller details to the database.
     *
     * @param dataControllerDO object which contain the details of the data controller to update.
     * @return the updated details of the data controller.
     */
    @Override
    public DataControllerDO updateDataController(DataControllerDO dataControllerDO) throws DataAccessException {

        DataControllerDO updatedDataControllerDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;

        String query = SQLQueries.DATA_CONTROLLER_UPDATE_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, dataControllerDO.getOrgName());
            preparedStatement.setString(2, dataControllerDO.getContactName());
            preparedStatement.setString(3, dataControllerDO.getStreet());
            preparedStatement.setString(4, dataControllerDO.getCountry());
            preparedStatement.setString(5, dataControllerDO.getEmail());
            preparedStatement.setString(6, dataControllerDO.getPhoneNo());
            preparedStatement.setString(7, dataControllerDO.getPublicKey());
            preparedStatement.setString(8, dataControllerDO.getPolicyUrl());
            preparedStatement.setInt(9, dataControllerDO.getDataControllerId());
            preparedStatement.executeUpdate();
            updatedDataControllerDO = getDataController(dataControllerDO.getDataControllerId());
        } catch (SQLException e) {
            log.error("Database error. Could not update the data controller.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_DATA_CONTROLLER_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_DATA_CONTROLLER_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return updatedDataControllerDO;
    }

    /**
     * Data Controller Configuration.
     * Delete data controller,mappings and returns the deleted data controller details.
     *
     * @param dataControllerId id of the data controller to delete.
     * @return the details of the deleted data controller.
     */
    @Override
    public DataControllerDO deleteDataController(int dataControllerId) throws DataAccessException {

        DataControllerDO dataController = new DataControllerDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        Savepoint savepoint = null;
        PreparedStatement selectPrepStat = null;
        PreparedStatement deleteUserMapPrepStat = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String selectQuery = SQLQueries.SELECT_DATA_CONTROLLER_BY_ID_QUERY;
        String deleteUserMapQuery = SQLQueries.DELETE_DATA_CONTROLLER_MAPPINGS_TO_USER;
        String query = SQLQueries.DELETE_DATA_CONTROLLER;
        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            selectPrepStat = connection.prepareCall(selectQuery);
            selectPrepStat.setInt(1, dataControllerId);
            resultSet = selectPrepStat.executeQuery();
            resultSet.first();
            dataController.setDataControllerId(resultSet.getInt(1));
            dataController.setOrgName(resultSet.getString(2));

            deleteUserMapPrepStat = connection.prepareStatement(deleteUserMapQuery);
            deleteUserMapPrepStat.setInt(1, dataControllerId);
            deleteUserMapPrepStat.executeUpdate();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, dataControllerId);
            preparedStatement.executeUpdate();
            connection.commit();
            log.info("Successfully deleted the Data Controller from the database");
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback the delete of data controller.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_DATA_CONTROLLER_DELETE_FAILURE.
                                getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_DATA_CONTROLLER_DELETE_FAILURE.
                                getErrorCode(), e1);
            }
            log.error("Database error. Could not delete data controller.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_DATA_CONTROLLER_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_DATA_CONTROLLER_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, deleteUserMapPrepStat);
            DBUtils.closeAllConnections(selectPrepStat, resultSet);
            DBUtils.closeAllConnections(preparedStatement);
        }
        return dataController;
    }

    /**
     * Personally Identifiable Info Category Configuration.
     * This method adds the personally identifiable info categories to the database.
     *
     * @param piiCategory object which contains the details of the pii category to add.
     * @return the details of the added pii category.
     */
    @Override
    public PiiCategoryDO addPiiCategory(PiiCategoryDO piiCategory) throws DataAccessException {

        PiiCategoryDO piiCategoryOut;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        PreparedStatement lastInsertIdPrepStat = null;
        ResultSet resultSet = null;
        Savepoint savepoint = null;

        String query = SQLQueries.ADD_PII_CATEGORY;
        String lastInsertIdQuery = SQLQueries.GET_LAST_INSERTED_PII_CAT_ID;
        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, piiCategory.getPiiCat());
            preparedStatement.setString(2, piiCategory.getPiiCatDescription());
            preparedStatement.setInt(3, piiCategory.getSensitivity());
            preparedStatement.executeUpdate();
            connection.commit();

            lastInsertIdPrepStat = connection.prepareStatement(lastInsertIdQuery);
            resultSet = lastInsertIdPrepStat.executeQuery();
            resultSet.first();
            piiCategoryOut = getPersonalInfoCatById(resultSet.getInt(1));
            log.info("Personally Identifiable Information Category was successfully added to the database.");
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not roll back the PII category add.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PII_CAT_ADD_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PII_CAT_ADD_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not add personally identifiable info category to the database.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PII_CATEGORY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PII_CATEGORY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
            DBUtils.closeAllConnections(lastInsertIdPrepStat);
        }
        return piiCategoryOut;
    }

    /**
     * Personally Identifiable Info Category Configuration.
     * This method updates the personally identifiable info categories to the database.
     *
     * @param piiCategory object which contains the details of the pii category to update.
     * @return details of the updated pii category.
     */
    @Override
    public PiiCategoryDO updatePersonalInfoCat(PiiCategoryDO piiCategory) throws DataAccessException {

        PiiCategoryDO piiCategoryDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;

        String query = SQLQueries.PERSONALLY_IDENTIFIABLE_INFO_CAT_UPDATE_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, piiCategory.getPiiCat());
            preparedStatement.setString(2, piiCategory.getPiiCatDescription());
            preparedStatement.setInt(3, piiCategory.getSensitivity());
            preparedStatement.setInt(4, piiCategory.getPiiCatId());
            preparedStatement.executeUpdate();

            piiCategoryDO = getPersonalInfoCatById(piiCategory.getPiiCatId());
            log.info("Successfully updated the personally identifiable category to the database");
        } catch (SQLException e) {
            log.error("Database error. Could not update personally identifiable info category.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PII_CATEGORY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PII_CATEGORY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return piiCategoryDO;
    }

    /**
     * Personally Identifiable Info Categories Configuration.
     * Get personally identifiable info categories.
     *
     * @return a list of pii category details.
     */
    @Override
    public List<PiiCategoryDO> getPersonalInfoCatForConfig() throws DataAccessException {

        List<PiiCategoryDO> personallyIdentifiableInfoCatList = new ArrayList<>();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.SELECT_PII_CATEGORIES;
        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PiiCategoryDO piiCategoryDO = new PiiCategoryDO();
                piiCategoryDO.setPiiCatId(resultSet.getInt(1));
                piiCategoryDO.setPiiCat(resultSet.getString(2));
                piiCategoryDO.setPiiCatDescription(resultSet.getString(3));
                piiCategoryDO.setSensitivity(resultSet.getInt(4));
                personallyIdentifiableInfoCatList.add(piiCategoryDO);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get the personally identifiable info categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
        return personallyIdentifiableInfoCatList;
    }

    /**
     * Personally Identifiable Info Category Configuration.
     * Delete personally identifiable info category and return the category name.
     *
     * @param categoryId id of the pii category to delete.
     * @return details of the deleted pii category.
     */
    @Override
    public PiiCategoryDO deletePersonalInfoCat(int categoryId) throws DataAccessException {

        PiiCategoryDO piiCategory = new PiiCategoryDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement selectPreparedStatement = null;
        PreparedStatement deletePurposeMapPrepStat;
        PreparedStatement deletePreparedStatement = null;
        ResultSet resultSet = null;
        Savepoint savepoint = null;
        String selectQuery = SQLQueries.SELECT_PII_CAT_BY_ID;
        String deletePurposeMapQuery = SQLQueries.DELETE_PII_MAPPINGS_TO_PURPOSE;
        String deleteQuery = SQLQueries.DELETE_PII_CAT;

        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            selectPreparedStatement = connection.prepareStatement(selectQuery);
            selectPreparedStatement.setInt(1, categoryId);
            resultSet = selectPreparedStatement.executeQuery();
            resultSet.first();
            piiCategory.setPiiCat(resultSet.getString(1));

            deletePurposeMapPrepStat = connection.prepareStatement(deletePurposeMapQuery);
            deletePurposeMapPrepStat.setInt(1, categoryId);
            deletePurposeMapPrepStat.executeUpdate();

            deletePreparedStatement = connection.prepareStatement(deleteQuery);
            deletePreparedStatement.setInt(1, categoryId);
            deletePreparedStatement.executeUpdate();
            connection.commit();
            log.info("Successfully deleted the personally identifiable category from the database.");
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback the pii category delete.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PII_CAT_DELETE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PII_CAT_DELETE_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not delete the personally identifiable info category.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PII_CATEGORY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PII_CATEGORY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, deletePreparedStatement, selectPreparedStatement, resultSet);
        }
        return piiCategory;
    }

    /**
     * Get personally identifiable info category by id.
     *
     * @param catId id of the pii category.
     * @return piiCategory object which contains the details of the pii category for the above id.
     */
    @Override
    public PiiCategoryDO getPersonalInfoCatById(int catId) throws DataAccessException {

        PiiCategoryDO piiCategory = new PiiCategoryDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement selectPrepStat;
        ResultSet resultSet;

        String selectQuery = SQLQueries.GET_PII_CAT_BY_ID;
        try {
            selectPrepStat = connection.prepareStatement(selectQuery);
            selectPrepStat.setInt(1, catId);
            resultSet = selectPrepStat.executeQuery();
            resultSet.first();
            piiCategory.setPiiCatId(resultSet.getInt(1));
            piiCategory.setPiiCat(resultSet.getString(2));
            piiCategory.setPiiCatDescription(resultSet.getString(3));
            piiCategory.setSensitivity(resultSet.getInt(4));
        } catch (SQLException e) {
            log.error("Database error. Could not get the details of the personally identifiable info category.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_BY_ID_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_BY_ID_FAILURE.getErrorCode(), e);
        }
        return piiCategory;
    }

    /**
     * Purpose Configuration.
     * Get purpose details from the database.
     *
     * @return a list of purpose details.
     */
    @Override
    public List<PurposeDetailsDO> getPurposesForConfig() throws DataAccessException {

        List<PurposeDetailsDO> purposeDetailsDOList = new ArrayList<>();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Savepoint savepoint = null;

        String query = SQLQueries.GET_PURPOSES;
        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PurposeDetailsDO purposeDetailsDO = new PurposeDetailsDO();
                purposeDetailsDO.setPurposeId(resultSet.getInt(1));
                purposeDetailsDO.setPurpose(resultSet.getString(2));

                PurposeCategoryDO[] purposeCategoryDOArr = getPurposeCatsForPurposeConf(connection, resultSet
                        .getInt(1)).toArray(new PurposeCategoryDO[0]);
                purposeDetailsDO.setPurposeCategoryDOArr(purposeCategoryDOArr);

                purposeDetailsDO.setPrimaryPurpose(resultSet.getString(3));
                purposeDetailsDO.setTermination(resultSet.getString(4));
                purposeDetailsDO.setThirdPartyDis(resultSet.getString(5));
                purposeDetailsDO.setThirdPartyId(resultSet.getInt(6));
                purposeDetailsDO.setthirdPartyName(resultSet.getString(7));

                PiiCategoryDO[] piiCategoryDOArr = getPersonalIdentifyCatForPurposeConf(connection, resultSet
                        .getInt(1))
                        .toArray(new PiiCategoryDO[0]);
                purposeDetailsDO.setpiiCategoryArr(piiCategoryDOArr);
                purposeDetailsDOList.add(purposeDetailsDO);
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not get the purpose details.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_GET_PURPOSE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_GET_PURPOSE_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not get purpose details for config.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSES_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
        return purposeDetailsDOList;
    }

    private List<PurposeCategoryDO> getPurposeCatsForPurposeConf(Connection connection, int purposeId) throws
            DataAccessException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.PURPOSE_CATS_FOR_PURPOSE_CONF_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, purposeId);
            resultSet = preparedStatement.executeQuery();
            List<PurposeCategoryDO> purposeCatList = new ArrayList<>();
            while (resultSet.next()) {
                PurposeCategoryDO purposeCat = new PurposeCategoryDO();
                purposeCat.setPurposeId(resultSet.getInt(1));
                purposeCat.setPurposeCatId(resultSet.getInt(2));
                purposeCat.setPurposeCatShortCode(resultSet.getString(3));
                purposeCatList.add(purposeCat);
            }
            return purposeCatList;
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATS_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATS_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement, resultSet);
        }
    }

    private List<PiiCategoryDO> getPersonalIdentifyCatForPurposeConf(Connection connection, int purposeId) throws
            DataAccessException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.PERSONALLY_IDENTIFIABLR_CAT_FOR_PURPOSE_CONF_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, purposeId);
            resultSet = preparedStatement.executeQuery();
            List<PiiCategoryDO> piiCategoryDOList = new ArrayList<>();
            while (resultSet.next()) {
                PiiCategoryDO piiCategoryDO = new PiiCategoryDO();
                piiCategoryDO.setPurposeId(resultSet.getInt(1));
                piiCategoryDO.setPiiCatId(resultSet.getInt(2));
                piiCategoryDO.setPiiCat(resultSet.getString(3));
                piiCategoryDOList.add(piiCategoryDO);
            }
            return piiCategoryDOList;
        } catch (SQLException e) {
            log.error("Database error. Could not get personally identifiable categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement, resultSet);
        }

    }

    /**
     * Purpose Configuration.
     * This method adds purpose details to the database.
     *
     * @param purpose object which contains the details of a purpose to add.
     * @return details of the added purpose.
     */
    @Override
    public PurposeDetailsDO addPurposeDetails(PurposeDetailsDO purpose) throws DataAccessException {

        PurposeDetailsDO purposeDetails;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        PreparedStatement purposeIdPrepStat = null;
        ResultSet resultSet = null;
        Savepoint savepoint = null;

        String query = SQLQueries.ADD_PURPOSE;
        String purposeIdQuery = SQLQueries.SELECT_PURPOSE_ID_QUERY;
        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, purpose.getPurpose());
            preparedStatement.setString(2, purpose.getPrimaryPurpose());
            preparedStatement.setString(3, purpose.getTermination());
            preparedStatement.setString(4, purpose.getThirdPartyDis());
            preparedStatement.setInt(5, purpose.getThirdPartyId());
            preparedStatement.executeUpdate();

            purposeIdPrepStat = connection.prepareStatement(purposeIdQuery);
            purposeIdPrepStat.setString(1, purpose.getPurpose());
            resultSet = purposeIdPrepStat.executeQuery();
            resultSet.first();
            mapPurposeWithPurposeCategories(connection, resultSet.getInt(1), purpose.getPurposeCategoryDOArr());
            mapPurposeWithPersonalInfoCategories(connection, resultSet.getInt(1), purpose.getpiiCategoryArr());
            connection.commit();
            purposeDetails = getPurposeDetailsById(resultSet.getInt(1));
            log.info("Successfully added the purpose details to the database");
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback purpose add.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PURPOSE_ADD_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PURPOSE_ADD_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not add purpose details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PURPOSE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PURPOSE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, purposeIdPrepStat, resultSet);
        }
        return purposeDetails;
    }

    private void mapPurposeWithPurposeCategories(Connection connection, int purposeId, PurposeCategoryDO[]
            purposeCategories) throws DataAccessException {

        PreparedStatement preparedStatement = null;
        String query = SQLQueries.ADD_PURPOSE_MAPPING_WITH_PURPOSE_CAT;
        try {
            preparedStatement = connection.prepareStatement(query);
            for (PurposeCategoryDO purposeCategory : purposeCategories) {
                preparedStatement.setInt(1, purposeId);
                preparedStatement.setInt(2, purposeCategory.getPurposeCatId());
                preparedStatement.setInt(3, purposeCategory.getPurposeCatId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Database error. Could not map purpose to purpose category.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_MAP_PURPOSE_AND_PURPOSE_CAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_MAP_PURPOSE_AND_PURPOSE_CAT_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement);
        }
    }

    private void mapPurposeWithPersonalInfoCategories(Connection connection, int purposeId, PiiCategoryDO[]
            piiCategories) throws DataAccessException {

        PreparedStatement preparedStatement = null;
        String query = SQLQueries.ADD_PURPOSE_MAPPING_WITH_PII_CATEGORY;
        try {
            for (PiiCategoryDO piiCategory : piiCategories) {
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, purposeId);
                preparedStatement.setInt(2, piiCategory.getPiiCatId());
                preparedStatement.setInt(3, piiCategory.getPiiCatId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Database error. Could not map purpose to personally identifiable info category.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_MAP_PURPOSE_AND_PII_CAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_MAP_PURPOSE_AND_PII_CAT_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement);
        }
    }

    /**
     * Purpose Configuration.
     * This method updates the purpose details to the database.
     *
     * @param purpose details of the purpose to update.
     * @return updated details of the purpose.
     */
    @Override
    public PurposeDetailsDO updatePurposeDetails(PurposeDetailsDO purpose) throws DataAccessException {

        PurposeDetailsDO purposeDetailsDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        Savepoint savepoint = null;

        String query = SQLQueries.PURPOSE_DETAILS_UPDATE_QUERY;
        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, purpose.getPurpose());
            preparedStatement.setString(2, purpose.getPrimaryPurpose());
            preparedStatement.setString(3, purpose.getTermination());
            preparedStatement.setString(4, purpose.getThirdPartyDis());
            preparedStatement.setInt(5, purpose.getThirdPartyId());
            preparedStatement.setInt(6, purpose.getPurposeId());
            preparedStatement.executeUpdate();

            deleteMappingsWithPurpose(connection, purpose.getPurposeId());
            mapPurposeWithPurposeCategories(connection, purpose.getPurposeId(), purpose.getPurposeCategoryDOArr());
            mapPurposeWithPersonalInfoCategories(connection, purpose.getPurposeId(), purpose.getpiiCategoryArr());
            connection.commit();
            purposeDetailsDO = getPurposeDetailsById(purpose.getPurposeId());
            log.info("Successfully updated the purpose details to the database.");
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback the purpose update.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PURPOSE_UPDATE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PURPOSE_UPDATE_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not update purpose details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PURPOSE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PURPOSE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return purposeDetailsDO;
    }

    private void deleteMappingsWithPurpose(Connection connection, int purposeId) throws DataAccessException {

        deletePurposeCatMapWithPurpose(connection, purposeId);
        deletePersonalInfoCatMapWithPurpose(connection, purposeId);
    }

    private void deletePurposeCatMapWithPurpose(Connection connection, int purposeId) throws DataAccessException {

        PreparedStatement deletePurposeCatStat = null;
        String deletePurposeCat = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_PURPOSE_CAT;
        try {
            deletePurposeCatStat = connection.prepareStatement(deletePurposeCat);
            deletePurposeCatStat.setInt(1, purposeId);
            deletePurposeCatStat.executeUpdate();
            deletePersonalInfoCatMapWithPurpose(connection, purposeId);
        } catch (SQLException e) {
            log.error("Database error. Could not delete purpose map with purpose categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_CAT_MAP_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_CAT_MAP_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(deletePurposeCatStat);
        }
    }

    private void deletePersonalInfoCatMapWithPurpose(Connection connection, int purposeId) throws DataAccessException {

        PreparedStatement deletePersonalInfoCatStat = null;
        String deletePersonalInfoCat = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_PII_CAT;
        try {
            deletePersonalInfoCatStat = connection.prepareStatement(deletePersonalInfoCat);
            deletePersonalInfoCatStat.setInt(1, purposeId);
            deletePersonalInfoCatStat.executeUpdate();
        } catch (SQLException e) {
            log.error("Database error. Could not delete purpose map with personally identifiable categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_PII_CAT_MAP_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_PII_CAT_MAP_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(deletePersonalInfoCatStat);
        }
    }

    /**
     * Purpose Configuration.
     * Delete a purpose from the database and return the deleted purpose name.
     *
     * @param purposeId id of a purpose to delete.
     * @return details of the deleted purpose.
     */
    @Override
    public PurposeDetailsDO deletePurpose(int purposeId) throws DataAccessException {

        PurposeDetailsDO purpose = new PurposeDetailsDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        Savepoint savepoint = null;
        PreparedStatement selectPreparedStatement;
        PreparedStatement deletePIIMapPrepStat;
        PreparedStatement deletePurposeCatMapPrepStat;
        PreparedStatement deleteServiceMapPrepStat;
        PreparedStatement deleteConsentMapPrepStat;
        PreparedStatement deletePreparedStatement;
        ResultSet resultSet;

        String selectQuery = SQLQueries.SELECT_PURPOSE_NAME_BY_ID;
        String deletePIIMapQuery = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_PII_CAT;
        String deletePurposeCatMapQuery = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_PURPOSE_CAT;
        String deleteServiceMapQuery = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_SERVICE;
        String deleteConsentMapQuery = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_CONSENT;
        String deleteQuery = SQLQueries.DELETE_PURPOSE;
        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            selectPreparedStatement = connection.prepareStatement(selectQuery);
            selectPreparedStatement.setInt(1, purposeId);
            resultSet = selectPreparedStatement.executeQuery();
            resultSet.first();
            purpose.setPurpose(resultSet.getString(1));

            deletePIIMapPrepStat = connection.prepareStatement(deletePIIMapQuery);
            deletePIIMapPrepStat.setInt(1, purposeId);
            deletePIIMapPrepStat.executeUpdate();

            deletePurposeCatMapPrepStat = connection.prepareStatement(deletePurposeCatMapQuery);
            deletePurposeCatMapPrepStat.setInt(1, purposeId);
            deletePurposeCatMapPrepStat.executeUpdate();

            deleteServiceMapPrepStat = connection.prepareStatement(deleteServiceMapQuery);
            deleteServiceMapPrepStat.setInt(1, purposeId);
            deleteServiceMapPrepStat.executeUpdate();

            deleteConsentMapPrepStat = connection.prepareStatement(deleteConsentMapQuery);
            deleteConsentMapPrepStat.setInt(1, purposeId);
            deleteConsentMapPrepStat.executeUpdate();

            deletePreparedStatement = connection.prepareStatement(deleteQuery);
            deletePreparedStatement.setInt(1, purposeId);
            deletePreparedStatement.executeUpdate();
            connection.commit();
            log.info("Successfully deleted the purpose from the database");
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback the purpose delete.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PURPOSE_DEL_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PURPOSE_DEL_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not delete the purpose.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_FAILURE.getErrorCode(), e);
        }

        return purpose;
    }

    /**
     * Get details of a purpose by purpose id.
     *
     * @param id of a purpose.
     * @return details of the purpose.
     */
    @Override
    public PurposeDetailsDO getPurposeDetailsById(int id) throws DataAccessException {

        PurposeDetailsDO purpose = new PurposeDetailsDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement selectPrepStat;
        ResultSet resultSet;

        String selectQuery = SQLQueries.SELECT_PURPOSE_DETAILS_BY_ID_QUERY;
        try {
            selectPrepStat = connection.prepareStatement(selectQuery);
            selectPrepStat.setInt(1, id);
            resultSet = selectPrepStat.executeQuery();
            resultSet.first();
            purpose.setPurposeId(resultSet.getInt(1));
            purpose.setPurpose(resultSet.getString(2));
            purpose.setPrimaryPurpose(resultSet.getString(3));
            purpose.setTermination(resultSet.getString(4));
            purpose.setThirdPartyDis(resultSet.getString(5));
            purpose.setThirdPartyId(resultSet.getInt(6));
            purpose.setthirdPartyName(resultSet.getString(7));

            PurposeCategoryDO[] purposeCategoryDOS = getPurposeCatsForPurposeConf(connection, id).toArray(new PurposeCategoryDO[0]);
            purpose.setPurposeCategoryDOArr(purposeCategoryDOS);

            PiiCategoryDO[] piiCategoryDOS = getPersonalIdentifyCatForPurposeConf(connection, id).toArray(new PiiCategoryDO[0]);
            purpose.setpiiCategoryArr(piiCategoryDOS);
        } catch (SQLException e) {
            log.error("Database error. Could not get details of the purpose.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FAILURE.getErrorCode(), e);
        }
        return purpose;
    }

    /**
     * Service Configuration.
     * This method adds services to the database.
     *
     * @param service object that contain the details of a service to add.
     * @return details of the added service.
     */
    @Override
    public ServicesDO addServiceDetails(ServicesDO service) throws DataAccessException {

        ServicesDO servicesDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        Savepoint savepoint = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement selectIdPrepStat;
        ResultSet resultSet;

        String query = SQLQueries.ADD_SERVICE;
        String selectIdQuery = SQLQueries.SELECT_LAST_INSERTED_SERVICE_QUERY;
        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, service.getServiceDescription());
            preparedStatement.executeUpdate();

            selectIdPrepStat = connection.prepareStatement(selectIdQuery);
            resultSet = selectIdPrepStat.executeQuery();
            resultSet.first();

            mapServiceWithPurposes(connection, resultSet.getInt(1), service);
            servicesDO = getServiceById(resultSet.getInt(1));
            connection.commit();
            log.info("Successfully added service details to the database");
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback the service add.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_SERVICE_ADD_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_SERVICE_ADD_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not add service details to the database.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_SERVICE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_SERVICE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return servicesDO;
    }

    private void mapServiceWithPurposes(Connection connection, int serviceId, ServicesDO services) throws
            DataAccessException {

        PreparedStatement preparedStatement = null;
        String query = SQLQueries.ADD_SERVICE_MAPPINGS_TO_PURPOSE_QUERY;
        try {
            for (PurposeDetailsDO purpose : services.getPurposeDetailsArr()) {
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, serviceId);
                preparedStatement.setInt(2, purpose.getPurposeId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Database error. Could not map service with purposes.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_SERVICE_MAP_PURPOSE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_SERVICE_MAP_PURPOSE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement);
        }
    }

    /**
     * Service Configuration.
     * This method updates the services to the database.
     *
     * @param service details of a service to update.
     * @return details of the updated service.
     */
    @Override
    public ServicesDO updateServiceDetails(ServicesDO service) throws DataAccessException {

        ServicesDO updatedServiceDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        Savepoint savepoint = null;

        String query = SQLQueries.UPDATE_SERVICE;
        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, service.getServiceDescription());
            preparedStatement.setInt(2, service.getServiceId());
            preparedStatement.executeUpdate();
            deleteServiceWithPurposesMap(connection, service.getServiceId());
            mapServiceWithPurposes(connection, service.getServiceId(), service);
            connection.commit();
            updatedServiceDO = getServiceById(service.getServiceId());
            log.info("Successfully updated the service details to the database");
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback service update.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_SERVICE_UPDATE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_SERVICE_UPDATE_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not update service details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_SERVICE_ERROR.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_SERVICE_ERROR.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return updatedServiceDO;
    }

    private void deleteServiceWithPurposesMap(Connection connection, int serviceId) throws DataAccessException {

        PreparedStatement preparedStatement = null;
        String query = SQLQueries.DELETE_SERVICE_MAPPING_TO_PURPOSE_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, serviceId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Database error. Could not delete the service mapping with purposes.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_SERVICE_MAP_PURPOSE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_SERVICE_MAP_PURPOSE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement);
        }
    }

    /**
     * Service Configuration.
     * Get the service details from the database.
     *
     * @return a list of services.
     */
    @Override
    public List<ServicesDO> getServicesForConf() throws DataAccessException {

        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        Savepoint savepoint = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.SERVICES_FOR_CONF_QUERY;
        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            List<ServicesDO> servicesDOList = new ArrayList<>();
            while (resultSet.next()) {
                ServicesDO servicesDO = new ServicesDO();
                servicesDO.setServiceId(resultSet.getInt(1));
                servicesDO.setServiceDescription(resultSet.getString(2));
                PurposeDetailsDO[] purposeDetailsDO = getPurposeDetailsForServiceConf(connection, resultSet.getInt
                        (1)).toArray(new PurposeDetailsDO[0]);
                servicesDO.setPurposeDetails(purposeDetailsDO);
                servicesDOList.add(servicesDO);
            }
            connection.commit();
            return servicesDOList;
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback the get services.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_SERVICE_GET_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_SERVICE_GET_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not get services for config.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICES_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
    }

    private List<PurposeDetailsDO> getPurposeDetailsForServiceConf(Connection connection, int serviceId) throws
            DataAccessException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.PURPOSE_DETAILS_FOR_SERVICE_CONF_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, serviceId);
            resultSet = preparedStatement.executeQuery();
            List<PurposeDetailsDO> purposeDetailsDOList = new ArrayList<>();
            while (resultSet.next()) {
                PurposeDetailsDO purposeDetailsDO = new PurposeDetailsDO();
                purposeDetailsDO.setPurposeId(resultSet.getInt(2));
                purposeDetailsDO.setPurpose(resultSet.getString(3));
                purposeDetailsDOList.add(purposeDetailsDO);
            }
            return purposeDetailsDOList;
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose details for service config.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FOR_SERVICE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FOR_SERVICE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement, resultSet);
        }
    }

    /**
     * Service Configuration.
     * Delete a service,mappings and return the deleted service name.
     *
     * @param serviceId id of a service to delete.
     * @return the details of the deleted service.
     */
    @Override
    public ServicesDO deleteService(int serviceId) throws DataAccessException {

        ServicesDO service = new ServicesDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        Savepoint savepoint = null;
        PreparedStatement selectPreparedStatement = null;
        PreparedStatement deletePurposeMapPrepStat;
        PreparedStatement deleteUserMapPrepStat;
        PreparedStatement deletePreparedStatement = null;
        ResultSet resultSet = null;

        String selectQuery = SQLQueries.SELECT_SERVICE_DES_BY_ID;
        String deletePurposeMapQuery = SQLQueries.DELETE_SERVICE_MAPPING_TO_PURPOSE_QUERY;
        String deleteUserMapQuery = SQLQueries.DELETE_SERVICE_MAPPING_TO_USER;
        String deleteQuery = SQLQueries.DELETE_SERVICE;

        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            selectPreparedStatement = connection.prepareStatement(selectQuery);
            selectPreparedStatement.setInt(1, serviceId);
            resultSet = selectPreparedStatement.executeQuery();
            resultSet.first();
            service.setServiceDescription(resultSet.getString(1));

            deletePurposeMapPrepStat = connection.prepareStatement(deletePurposeMapQuery);
            deletePurposeMapPrepStat.setInt(1, serviceId);
            deletePurposeMapPrepStat.executeUpdate();

            deleteUserMapPrepStat = connection.prepareStatement(deleteUserMapQuery);
            deleteUserMapPrepStat.setInt(1, serviceId);
            deleteUserMapPrepStat.executeUpdate();

            deletePreparedStatement = connection.prepareStatement(deleteQuery);
            deletePreparedStatement.setInt(1, serviceId);
            deletePreparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback service delete.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_SERVICE_DEL_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_SERVICE_DEL_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not delete the service.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_SERVICE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_SERVICE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, selectPreparedStatement, deletePreparedStatement, resultSet);
        }
        return service;
    }

    /**
     * This methods returns the service details by a service id.
     *
     * @param id of a service to get the service details.
     * @return details of the relevant service for the id.
     */
    @Override
    public ServicesDO getServiceById(int id) throws DataAccessException {

        ServicesDO servicesDO = new ServicesDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement selectPrepStat = null;
        ResultSet resultSet = null;

        String selectQuery = SQLQueries.GET_SERVICES_BY_ID_QUERY;
        try {
            selectPrepStat = connection.prepareStatement(selectQuery);
            selectPrepStat.setInt(1, id);
            resultSet = selectPrepStat.executeQuery();
            resultSet.first();
            servicesDO.setServiceId(resultSet.getInt(1));
            servicesDO.setServiceDescription(resultSet.getString(2));
            servicesDO.setPurposeDetails(getPurposeDetailsForServiceConf(connection, id).toArray(new
                    PurposeDetailsDO[0]));
        } catch (SQLException e) {
            log.error("Database error. Could not get the service details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICE_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, selectPrepStat, resultSet);
        }
        return servicesDO;
    }

    /**
     * User Consent Configuration.
     * This method revokes consent for one user.
     *
     * @param piiPrincipalId user name of the user.
     * @param serviceList    list of consents to be revoked.
     */
    @Override
    public void revokeConsentByUser(String piiPrincipalId, List<ServicesDO> serviceList) throws DataAccessException {

        ConsentDO consentDO = getSGUIDByUser(piiPrincipalId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String currentTime = dateFormat.format(calendar.getTime());

        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;

        String query = SQLQueries.CONSENT_BY_USER_REVOKE_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, currentTime);
            preparedStatement.setString(2, consentDO.getSGUID());
            for (ServicesDO aServiceList : serviceList) {
                preparedStatement.setInt(3, aServiceList.getServiceId());
                for (int j = 0; j < aServiceList.getPurposeDetailsArr().length; j++) {
                    if (aServiceList.getPurposeDetailsArr()[j].getPurposeId() != 0) {
                        preparedStatement.setInt(4, aServiceList.getPurposeDetailsArr()[j].
                                getPurposeId());
                        preparedStatement.executeUpdate();
                    }
                }
            }
            log.info("Successfully revoked the user consent from the database");
        } catch (SQLException e) {
            log.error("Database error. Could not revoke consent.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_REVOKE_CONSENT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_REVOKE_CONSENT_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
    }

    /**
     * This methods returns the details of the services in a simple way to view on an UI.
     *
     * @param subjectName username of the user.
     * @return details of the given consent of above user.
     */
    @Override
    public List<ServicesDO> getServicesForUserView(String subjectName) throws DataAccessException {

        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.SERVICES_FOR_USER_VIEW_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, subjectName);
            resultSet = preparedStatement.executeQuery();
            List<ServicesDO> servicesDOList = new ArrayList<>();
            while (resultSet.next()) {
                ServicesDO servicesDO = new ServicesDO();
                servicesDO.setServiceId(resultSet.getInt(1));
                servicesDO.setServiceDescription(resultSet.getString(2));

                List<Integer> purposeIdList = getPurposeIdByUserByService(connection, resultSet.getString(4),
                        resultSet.getInt(1));
                List<PurposeDetailsDO> purposeDetailsDOList = new ArrayList<>();
                for (Integer aPurposeIdList : purposeIdList) {
                    PurposeDetailsDO purpose = getPurposeDetailsByPurposeId(connection, aPurposeIdList);
                    purposeDetailsDOList.add(purpose);
                }
                servicesDO.setPurposeDetails(purposeDetailsDOList.toArray(new PurposeDetailsDO[0]));
                servicesDOList.add(servicesDO);
            }
            return servicesDOList;
        } catch (SQLException e) {
            log.error("Database error. Could not get services.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICES_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
    }

    private List<Integer> getPurposeIdByUserByService(Connection connection, String sguid, int serviceId) throws
            DataAccessException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.PURPOSE_ID_BY_USER_BY_SERVICE_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setString(2, sguid);
            resultSet = preparedStatement.executeQuery();

            List<Integer> purposeIdList = new ArrayList<>();
            while (resultSet.next()) {
                int i = resultSet.getInt(3);
                purposeIdList.add(i);
            }
            return purposeIdList;
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose IDs.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_ID_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_ID_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(preparedStatement, resultSet);
        }
    }

    /**
     * Purpose Category Configuration.
     * Get purpose category details from the database.
     *
     * @return a list of purpose categories.
     */
    @Override
    public List<PurposeCategoryDO> getPurposeCategories() throws DataAccessException {

        List<PurposeCategoryDO> purposeCategoryList = new ArrayList<>();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String query = SQLQueries.SELECT_PURPOSE_CATEGORIES_QUERY;

        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PurposeCategoryDO purposeCategory = new PurposeCategoryDO();
                purposeCategory.setPurposeCatId(resultSet.getInt(1));
                purposeCategory.setPurposeCatShortCode(resultSet.getString(2));
                purposeCategory.setPurposeCatDes(resultSet.getString(3));
                purposeCategoryList.add(purposeCategory);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get the purpose categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATEGORIES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATEGORIES_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
        return purposeCategoryList;
    }

    /**
     * Purpose Category Configuration.
     * Add purpose category details to the database.
     *
     * @param purposeCategory object that contains the details of a purpose category to add.
     * @return details of the added purpose category.
     */
    @Override
    public PurposeCategoryDO addPurposeCategory(PurposeCategoryDO purposeCategory) throws DataAccessException {

        PurposeCategoryDO addedPurposeCatDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        PreparedStatement selectIdPrepStat;
        ResultSet resultSet;

        String query = SQLQueries.ADD_PURPOSE_CATEGORY;
        String selectIdQuery = SQLQueries.SELECT_LAST_INSERTED_PURPOSE_CAT;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, purposeCategory.getPurposeCatShortCode());
            preparedStatement.setString(2, purposeCategory.getPurposeCatDes());
            preparedStatement.executeUpdate();
            log.info("Successfully added the Purpose Category to the database.");

            selectIdPrepStat = connection.prepareStatement(selectIdQuery);
            resultSet = selectIdPrepStat.executeQuery();
            resultSet.first();
            addedPurposeCatDO = getPurposeCategoryById(resultSet.getInt(1));
        } catch (SQLException e) {
            log.error("Database error. Could not add purpose category.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PURPOSE_CAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PURPOSE_CAT_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return addedPurposeCatDO;
    }

    /**
     * Purpose Category Configuration.
     * Update purpose category to the database.
     *
     * @param purposeCategory object that contains the details to update a purpose category.
     * @return details of the updated purpose category.
     */
    @Override
    public PurposeCategoryDO updatePurposeCategory(PurposeCategoryDO purposeCategory) throws DataAccessException {

        PurposeCategoryDO updatedPurposeCatDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;

        String query = SQLQueries.PURPOSE_CATEGORY_UPDATE_QUERY;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, purposeCategory.getPurposeCatShortCode());
            preparedStatement.setString(2, purposeCategory.getPurposeCatDes());
            preparedStatement.setInt(3, purposeCategory.getPurposeCatId());
            preparedStatement.executeUpdate();
            log.info("Successfully updated the Purpose Category to the database");
            updatedPurposeCatDO = getPurposeCategoryById(purposeCategory.getPurposeCatId());
        } catch (SQLException e) {
            log.error("Database error. Could not update purpose category.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PURPOSE_CAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PURPOSE_CAT_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return updatedPurposeCatDO;
    }

    /**
     * Purpose Category Configuration.
     * Delete purpose category,mappings to the purposes and return the purpose name.
     *
     * @param categoryId id of the purpose category to delete.
     * @return details of the deleted purpose category.
     */
    @Override
    public PurposeCategoryDO deletePurposeCategory(int categoryId) throws DataAccessException {

        PurposeCategoryDO purposeCategory = new PurposeCategoryDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement selectPreparedStatement = null;
        PreparedStatement deleteMappingsPreparedStatement;
        PreparedStatement deletePreparedStatement = null;
        ResultSet resultSet = null;
        Savepoint savepoint = null;

        String selectQuery = SQLQueries.GET_THIRD_PARTY_BY_ID_QUERY;
        String deleteMappingsQuery = SQLQueries.DELETE_PURPOSE_CATEGORY_MAPPINGS_QUERY;
        String deleteQuery = SQLQueries.DELETE_PURPOSE_CATEGORY;

        try {
            selectPreparedStatement = connection.prepareStatement(selectQuery);
            selectPreparedStatement.setInt(1, categoryId);
            resultSet = selectPreparedStatement.executeQuery();
            resultSet.first();
            purposeCategory.setPurposeCatShortCode(resultSet.getString(2));
            purposeCategory.setPurposeCatDes(resultSet.getString(3));

            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            deleteMappingsPreparedStatement = connection.prepareStatement(deleteMappingsQuery);
            deleteMappingsPreparedStatement.setInt(1, categoryId);
            deleteMappingsPreparedStatement.executeUpdate();

            deletePreparedStatement = connection.prepareStatement(deleteQuery);
            deletePreparedStatement.setInt(1, categoryId);
            deletePreparedStatement.executeUpdate();
            connection.commit();
            log.info("Successfully deleted the Purpose Category from the database");
        } catch (SQLException e) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e1) {
                log.error("Rollback error. Could not rollback the Purpose Category delete.", e1);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PURPOSE_CAT_DEL_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ROLLBACK_PURPOSE_CAT_DEL_FAILURE.getErrorCode(), e1);
            }
            log.error("Database error. Could not delete purpose category.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_CAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_CAT_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, selectPreparedStatement, deletePreparedStatement, resultSet);
        }
        return purposeCategory;
    }

    /**
     * This method returns the purpose category details by purpose category id.
     *
     * @param id id of a purpose category to get the purpose category details.
     * @return details of the purpose category.
     */
    @Override
    public PurposeCategoryDO getPurposeCategoryById(int id) throws DataAccessException {

        PurposeCategoryDO purposeCategoryDO = new PurposeCategoryDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement selectPrepStat = null;
        ResultSet resultSet = null;

        String selectQuery = SQLQueries.GET_PURPOSE_CATEGORY_BY_ID;
        try {
            selectPrepStat = connection.prepareStatement(selectQuery);
            selectPrepStat.setInt(1, id);
            resultSet = selectPrepStat.executeQuery();
            resultSet.first();
            purposeCategoryDO.setPurposeCatId(resultSet.getInt(1));
            purposeCategoryDO.setPurposeCatShortCode(resultSet.getString(2));
            purposeCategoryDO.setPurposeCatDes(resultSet.getString(3));
        } catch (SQLException e) {
            log.error("Database error. Could not get the purpose category details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CAT_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, selectPrepStat, resultSet);
        }
        return purposeCategoryDO;
    }

    /**
     * Third Party Configuration.
     * Get third party details from the database.
     *
     * @return a list of third parties.
     */
    @Override
    public List<ThirdPartyDO> getThirdPartyDetailsForConf() throws DataAccessException {

        List<ThirdPartyDO> thirdPartyList = new ArrayList<>();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = SQLQueries.SELECT_THIRD_PARTIES_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ThirdPartyDO thirdParty = new ThirdPartyDO();
                thirdParty.setThirdPartyId(resultSet.getInt(1));
                thirdParty.setThirdPartyName(resultSet.getString(2));
                thirdPartyList.add(thirdParty);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get third party details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTIES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTIES_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement, resultSet);
        }
        return thirdPartyList;
    }

    /**
     * Third Party Configuration.
     * Add third party details to the database.
     *
     * @param thirdParty object contains the details of a third party to add.
     * @return details of the added third party.
     */
    @Override
    public ThirdPartyDO addThirdParty(ThirdPartyDO thirdParty) throws DataAccessException {

        ThirdPartyDO addedThirdPartyDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        PreparedStatement selectIdPrepStat;
        ResultSet resultSet;

        String query = SQLQueries.ADD_THIRD_PARTY_QUERY;
        String selectIdQuery = SQLQueries.SELECT_LAST_INSERTED_THIRD_PARTY_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, thirdParty.getThirdPartyName());
            preparedStatement.executeUpdate();
            log.info("Successfully added the Third Party to the database.");

            selectIdPrepStat = connection.prepareStatement(selectIdQuery);
            resultSet = selectIdPrepStat.executeQuery();
            resultSet.first();
            addedThirdPartyDO = getThirdPartyById(resultSet.getInt(1));
        } catch (SQLException e) {
            log.error("Database error. Could not add the third party details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_THIRD_PARTY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_THIRD_PARTY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return addedThirdPartyDO;
    }

    /**
     * Third Party Configuration.
     * Update the third party details to the database.
     *
     * @param thirdParty object that contains the details of a third party to update.
     * @return details of the updated third party.
     */
    @Override
    public ThirdPartyDO updateThirdParty(ThirdPartyDO thirdParty) throws DataAccessException {

        ThirdPartyDO updatedThirdPartyDO;
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement preparedStatement = null;
        String query = SQLQueries.UPDATE_THIRD_PARTY_QUERY;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, thirdParty.getThirdPartyName());
            preparedStatement.setInt(2, thirdParty.getThirdPartyId());
            preparedStatement.executeUpdate();
            log.info("Successfully updated the Third Party to the database");
            updatedThirdPartyDO = getThirdPartyById(thirdParty.getThirdPartyId());
        } catch (SQLException e) {
            log.error("Database error. Could not update the third party.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_THIRD_PARTY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_THIRD_PARTY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, preparedStatement);
        }
        return updatedThirdPartyDO;
    }

    /**
     * This method returns the details of third party by third party id.
     *
     * @param id of the third party to get the third party details.
     * @return details of the third party.
     */
    @Override
    public ThirdPartyDO getThirdPartyById(int id) throws DataAccessException {

        ThirdPartyDO thirdPartyDO = new ThirdPartyDO();
        Connection connection = JDBCPersistenceManager.getInstance().getDBConnection();
        PreparedStatement selectPrepStat = null;
        ResultSet resultSet = null;

        String selectQuery = SQLQueries.GET_THIRD_PARTY_BY_ID_QUERY;

        try {
            selectPrepStat = connection.prepareStatement(selectQuery);
            selectPrepStat.setInt(1, id);
            resultSet = selectPrepStat.executeQuery();
            resultSet.first();
            thirdPartyDO.setThirdPartyId(resultSet.getInt(1));
            thirdPartyDO.setThirdPartyName(resultSet.getString(2));
        } catch (SQLException e) {
            log.error("Database error. Could not get the details of the Third Party.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTY_FAILURE.getErrorCode(), e);
        } finally {
            DBUtils.closeAllConnections(connection, selectPrepStat, resultSet);
        }
        return thirdPartyDO;
    }
}
