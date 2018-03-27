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
import org.wso2.identity.carbon.user.consent.mgt.backend.model.*;

import java.sql.*;
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

        ConsentDO consentDO;
        DataControllerDO dataController;
        consents = new ArrayList<>();
        String query = SQLQueries.TRANSACTION_AND_DATA_CONTROLLER_DETAILS_QUERY;

        try (Connection con = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, piiPrincipalId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                }
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get the user and data controller details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_AND_DATA_CONTROLLER_FAILURE.
                            getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_AND_DATA_CONTROLLER_FAILURE.
                            getErrorCode(), e);
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

        ServicesDO services;
        serviceList = new ArrayList<>();
        String query = SQLQueries.SERVICES_DETAILS_BY_USER_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, sguid);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {

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
                }
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get consent details for the user.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FAILURE.getErrorCode(), e);
        }
    }

    /**
     * This constructor gets the services and purposes by third party for one user.
     *
     * @param sguid        is the system generated user id for the user.
     * @param thirdPartyId is the id of the third party.
     */
    public ConsentDaoImpl(String sguid, int thirdPartyId) throws DataAccessException {

        ServicesDO services;
        PiiCategoryDO piiCategory;
        serviceList = new ArrayList<>();
        String query = SQLQueries.SERVICE_DETAILS_BY_USER_AND_THIRD_PARTY_QUERY;

        try (Connection con = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, sguid);
                preparedStatement.setInt(2, thirdPartyId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {

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
                }
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get consent details of the user to third party.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FOR_THIRD_PARTY_FAILURE.
                            getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FOR_THIRD_PARTY_FAILURE.
                            getErrorCode(), e);
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
        String query = SQLQueries.CONSENT_DETAILS_BY_USER_BY_THIRD_PARTY_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, user.getSGUID());
                preparedStatement.setInt(2, thirdPartyId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    List<ServicesDO> servicesDOList = new ArrayList<>();
                    while (resultSet.next()) {
                        ServicesDO servicesDO = new ServicesDO();
                        servicesDO.setServiceId(resultSet.getInt(2));
                        servicesDO.setServiceDescription(resultSet.getString(3));
                        List<Integer> purposeIdList = getPurposeIdByUserByServiceByThirdParty(connection, user
                                        .getSGUID(),
                                resultSet
                                        .getInt(2), thirdPartyId);
                        List<PurposeDetailsDO> purposeDetailsDOList = new ArrayList<>();
                        for (Integer aPurposeIdList : purposeIdList) {
                            PurposeDetailsDO purposeDetailsDO = getPurposeDetailsByPurposeId(connection,
                                    aPurposeIdList);
                            purposeDetailsDOList.add(purposeDetailsDO);
                        }
                        servicesDO.setPurposeDetails(purposeDetailsDOList.toArray(new PurposeDetailsDO[0]));
                        servicesDOList.add(servicesDO);
                    }
                    return servicesDOList;
                }
            }
        } catch (SQLException | DataAccessException e) {
            log.error("Database error. Could not get consent details of the user to third party.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FOR_THIRD_PARTY_FAILURE.
                            getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_USER_CONSENT_FOR_THIRD_PARTY_FAILURE.
                            getErrorCode(), e);
        }
    }

    private List<Integer> getPurposeIdByUserByServiceByThirdParty(Connection connection, String sguid, int serviceId,
                                                                  int thirdPartyId) throws DataAccessException {

        String query = SQLQueries.PURPOSE_ID_BY_USER_BY_SERVICE_BY_THIRD_PARTY_QUERY;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setString(2, sguid);
            preparedStatement.setInt(3, thirdPartyId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Integer> purposeIdList = new ArrayList<>();
                while (resultSet.next()) {
                    int i = resultSet.getInt(3);
                    purposeIdList.add(i);
                }
                return purposeIdList;
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose IDs for services.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_IDS_FOR_SERVICES_FAILURE.
                            getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_IDS_FOR_SERVICES_FAILURE.
                            getErrorCode(), e);
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

        String query = SQLQueries.GET_INTERNAL_ID_BY_USER;

        try (Connection con = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, piiPrincipalId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ConsentDO consentDO = new ConsentDO();
                    resultSet.next();
                    consentDO.setSGUID(resultSet.getString(1));
                    return consentDO;
                }
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get internal ID for the user.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_INTERNAL_ID_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_INTERNAL_ID_FAILURE.getErrorCode(), e);
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

        Savepoint savepoint;
        String query = SQLQueries.ADD_USER_DETAILS;

        try (Connection con = JDBCPersistenceManager.getInstance().getDBConnection()) {
            con.setAutoCommit(false);
            savepoint = con.setSavepoint();
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, consentDO.getCollectionMethod());
                preparedStatement.setString(2, consentDO.getSGUID());
                preparedStatement.setString(3, consentDO.getPiiPrincipalId());
                preparedStatement.setString(4, consentDO.getConsentTimestamp());
                preparedStatement.setInt(5, consentDO.getDataController().getDataControllerId());
                preparedStatement.executeUpdate();
                addUserConsentDetails(con, consentDO, services);
                con.commit();
                log.info("Successfully added the user " + consentDO.getPiiPrincipalId() + " to the database");
            } catch (SQLException | DataAccessException e) {
                con.rollback(savepoint);
                log.error("Database error. Could not add the user consents to the database.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_USER_CONSENT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_USER_CONSENT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not close the database connection", e);
            throw new DataAccessException("Database error. Could not close the database connection", e);
        }
    }

    private void addUserConsentDetails(Connection connection, ConsentDO consentDO, ServicesDO[] services) throws
            DataAccessException {

        String query = SQLQueries.ADD_USER_CONSENTS;

        for (ServicesDO service : services) {
            for (int i = 0; i < service.getPurposeDetailsArr().length; i++) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, consentDO.getSGUID());
                    preparedStatement.setInt(2, service.getServiceId());
                    preparedStatement.setInt(3, service.getPurposeDetailsArr()[i].getPurposeId());
                    preparedStatement.setString(4, getPurposeTerminationDays(connection, service
                            .getPurposeDetailsArr()[i].getPurposeId(), service.getPurposeDetailsArr()[i]
                            .getTimestamp()));
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
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_USER_CONSENT_FAILURE.
                                    getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_USER_CONSENT_FAILURE.
                                    getErrorCode(), e);
                }
            }
        }
    }

    private String getPurposeTerminationDays(Connection connection, int purposeId, String consentedTime) throws
            DataAccessException {

        String exactTermination;
        String query = SQLQueries.GET_TERMINATION_BY_PURPOSE;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, purposeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(consentedTime));
                cal.add(Calendar.DATE, resultSet.getInt(1));
                exactTermination = dateFormat.format(cal.getTime());
            } catch (ParseException e) {
                log.error("Time error. consented time is not in required date format.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DATE_PARSE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DATE_PARSE_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose termination days.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_TERMINATION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_TERMINATION_FAILURE.getErrorCode(), e);
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

        ConsentDaoImpl consentDaoImpl = new ConsentDaoImpl();
        ConsentDO consentDO = consentDaoImpl.getSGUIDByUser(subjectName);
        String query = SQLQueries.SERVICE_BY_USER_BY_SERVICE_ID_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, consentDO.getSGUID());
                preparedStatement.setInt(2, serviceId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {

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
                } catch (SQLException | DataAccessException e) {
                    log.error("Database error. Could not get services for the user.", e);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_CONSENT_SERVICES_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_CONSENT_SERVICES_FAILURE
                                    .getErrorCode(), e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
    }

    private PurposeDetailsDO getPurposeDetailsByPurposeId(Connection connection, int purposeId) throws
            DataAccessException {

        String query = SQLQueries.GET_PURPOSE_BY_ID;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, purposeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.first();

                PurposeDetailsDO purposeDetailsDO = new PurposeDetailsDO();
                purposeDetailsDO.setPurposeId(resultSet.getInt(1));
                purposeDetailsDO.setPurpose(resultSet.getString(2));
                purposeDetailsDO.setPrimaryPurpose(String.valueOf(resultSet.getInt(4)));
                purposeDetailsDO.setTermination(String.valueOf(resultSet.getInt(5)));
                purposeDetailsDO.setThirdPartyDis(String.valueOf(resultSet.getInt(6)));
                purposeDetailsDO.setThirdPartyId(resultSet.getInt(7));
                purposeDetailsDO.setThirdPartyName(resultSet.getString(8));

                PurposeCategoryDO[] purposeCategoryDOArr = getPurposeCatsForPurposeConf(connection, resultSet.getInt(1))
                        .toArray(new PurposeCategoryDO[0]);
                purposeDetailsDO.setPurposeCategoryDOArr(purposeCategoryDOArr);

                PiiCategoryDO[] piiCategoryDOArr = getPersonalIdentifyCatForPurposeConf(connection, resultSet.getInt(1))
                        .toArray(new PiiCategoryDO[0]);
                purposeDetailsDO.setpiiCategoryArr(piiCategoryDOArr);
                return purposeDetailsDO;
            } catch (SQLException | DataAccessException e) {
                log.error("Database error. Could not get purpose.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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

        ConsentDaoImpl consentDaoImpl = new ConsentDaoImpl();
        ConsentDO consentDO = consentDaoImpl.getSGUIDByUser(subjectName);
        String query = SQLQueries.PURPOSE_BY_USER_BY_SERVICE_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, consentDO.getSGUID());
                preparedStatement.setInt(2, serviceId);
                preparedStatement.setInt(3, purposeId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.first();
                    return getPurposeDetailsByPurposeId(connection, resultSet.getInt(4));
                } catch (SQLException | DataAccessException e) {
                    log.error("Database error. Could not get purpose details for the user by service.", e);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FOR_USER_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FOR_USER_FAILURE
                                    .getErrorCode(), e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.IS_PII_PRINCIPAL_EXISTS;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, piiPrincipalId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    i = resultSet.getByte("count");
                } catch (SQLException e) {
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode()
                            , e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get the existence of the user in the database.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_USER_NAME_EXISTS_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_USER_NAME_EXISTS_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.GET_DATA_CONTROLLER_ID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, orgName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        dataControllerId = 0;
                    } else {
                        dataControllerId = resultSet.getInt("DATA_CONTROLLER_ID");
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode(),
                            e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);

            }
        } catch (SQLException e) {
            log.error("Database error. Could not get the existence of the data controller.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DATA_CONTROLLER_EXISTS_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DATA_CONTROLLER_EXISTS_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.GET_SERVICE_ID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, serviceName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        serviceId = 0;
                    } else {
                        serviceId = resultSet.getInt("SERVICE_ID");
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode(),
                            e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get service ID.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICE_ID_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICE_ID_FAILURE.getErrorCode(), e);
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

        String query = SQLQueries.GET_PURPOSE_ID;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, purposeName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        purposeId = 0;
                    } else {
                        purposeId = resultSet.getInt("PURPOSE_ID");
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode()
                            , e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose ID.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_ID_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_ID_FAILURE.getErrorCode(), e);
        }
        return purposeId;
    }

    /**
     * JSON Parser Methods.
     * This method gets the sensitive personal info category for one user.
     *
     * @param sguid System Generated User Id for a user.
     * @return the sensitive personal info categories.
     */
    @Override
    public List<PiiCategoryDO> getSensitivePersonalInfoCategory(String sguid) throws DataAccessException {

        String query = SQLQueries.SENSITIVE_PERSONAL_INFO_CATEGORY_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, sguid);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode()
                            , e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get the sensitive personal info categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SENSITIVE_PII_CATEGORIES_FAILURE
                            .getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SENSITIVE_PII_CATEGORIES_FAILURE
                            .getErrorCode(), e);
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

        String query = SQLQueries.PURPOSE_CATEGORIES_QUERY;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, sguid);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode()
                            , e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get purpose categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATEGORIES_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATEGORIES_FAILURE.getErrorCode(), e);
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

        String query = SQLQueries.SELECT_DATA_CONTROLLER_BY_ID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, dataControllerId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode()
                            , e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get the data controller details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_DATA_CONTROLLER_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_DATA_CONTROLLER_FAILURE.getErrorCode(), e);
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
        String selectAllQuery = SQLQueries.SELECT_DATA_CONTROLLERS;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement selectAllPrepStat = connection.prepareStatement(selectAllQuery)) {
                try (ResultSet resultSet = selectAllPrepStat.executeQuery()) {
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
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode()
                            , e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not get data controllers' details.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_DATA_CONTROLLERS_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_DATA_CONTROLLERS_FAILURE.getErrorCode(), e);
        }
        return dataControllerList;
    }

    /**
     * Data Controller Configuration.
     * Add the Data Controller details to the database.
     *
     * @param dataController object which contains the details of a data controller.
     * @return the added details of the data controller.
     */
    @Override
    public DataControllerDO addDataController(DataControllerDO dataController) throws DataAccessException {

        DataControllerDO addedDataControllerDO;
        String query = SQLQueries.ADD_DATA_CONTROLLER;
        String selectIdQuery = SQLQueries.GET_LAST_INSERTED_DATA_CONTROLLER_ID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 PreparedStatement selectIdPrepStat = connection.prepareStatement(selectIdQuery)) {
                preparedStatement.setString(1, dataController.getOrgName());
                preparedStatement.setString(2, dataController.getContactName());
                preparedStatement.setString(3, dataController.getStreet());
                preparedStatement.setString(4, dataController.getCountry());
                preparedStatement.setString(5, dataController.getEmail());
                preparedStatement.setString(6, dataController.getPhoneNo());
                preparedStatement.setString(7, dataController.getPublicKey());
                preparedStatement.setString(8, dataController.getPolicyUrl());
                preparedStatement.executeUpdate();

                try (ResultSet resultSet = selectIdPrepStat.executeQuery()) {
                    resultSet.first();
                    addedDataControllerDO = getDataController(resultSet.getInt(1));
                    log.info("Successfully added the data controller details to the database");
                }
            } catch (SQLException | DataAccessException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not add data controller details to the database.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_DATA_CONTROLLER_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_DATA_CONTROLLER_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.DATA_CONTROLLER_UPDATE_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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
            } catch (SQLException | DataAccessException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            log.error("Database error. Could not update the data controller.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_DATA_CONTROLLER_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_DATA_CONTROLLER_FAILURE.getErrorCode(), e);
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
        Savepoint savepoint;
        String selectQuery = SQLQueries.SELECT_DATA_CONTROLLER_BY_ID_QUERY;
        String deleteUserMapQuery = SQLQueries.DELETE_DATA_CONTROLLER_MAPPINGS_TO_USER;
        String query = SQLQueries.DELETE_DATA_CONTROLLER;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement selectPrepStat = connection.prepareCall(selectQuery);
                 PreparedStatement deleteUserMapPrepStat = connection.prepareStatement(deleteUserMapQuery);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                selectPrepStat.setInt(1, dataControllerId);
                try (ResultSet resultSet = selectPrepStat.executeQuery()) {
                    resultSet.first();
                    dataController.setDataControllerId(resultSet.getInt(1));
                    dataController.setOrgName(resultSet.getString(2));
                }
                deleteUserMapPrepStat.setInt(1, dataControllerId);
                deleteUserMapPrepStat.executeUpdate();

                preparedStatement.setInt(1, dataControllerId);
                preparedStatement.executeUpdate();
                connection.commit();
                log.info("Successfully deleted the Data Controller from the database");
            } catch (SQLException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not delete data controller.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_DATA_CONTROLLER_FAILURE
                                .getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_DATA_CONTROLLER_FAILURE.getErrorCode(),
                        e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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
        Savepoint savepoint;
        String query = SQLQueries.ADD_PII_CATEGORY;
        String lastInsertIdQuery = SQLQueries.GET_LAST_INSERTED_PII_CAT_ID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 PreparedStatement lastInsertIdPrepStat = connection.prepareStatement(lastInsertIdQuery)) {
                preparedStatement.setString(1, piiCategory.getPiiCat());
                preparedStatement.setString(2, piiCategory.getPiiCatDescription());
                preparedStatement.setInt(3, piiCategory.getSensitivity());
                preparedStatement.executeUpdate();

                try (ResultSet resultSet = lastInsertIdPrepStat.executeQuery()) {
                    resultSet.first();
                    piiCategoryOut = getPIICatById(connection, resultSet.getInt(1));
                }
                connection.commit();
                log.info("Personally Identifiable Information Category was successfully added to the database.");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not add personally identifiable info category to the database.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PII_CATEGORY_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PII_CATEGORY_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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
        Savepoint savepoint;
        String query = SQLQueries.PERSONALLY_IDENTIFIABLE_INFO_CAT_UPDATE_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, piiCategory.getPiiCat());
                preparedStatement.setString(2, piiCategory.getPiiCatDescription());
                preparedStatement.setInt(3, piiCategory.getSensitivity());
                preparedStatement.setInt(4, piiCategory.getPiiCatId());
                preparedStatement.executeUpdate();

                piiCategoryDO = getPIICatById(connection, piiCategory.getPiiCatId());
                connection.commit();
                log.info("Successfully updated the personally identifiable category to the database");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not update personally identifiable info category.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PII_CATEGORY_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PII_CATEGORY_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.SELECT_PII_CATEGORIES;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        PiiCategoryDO piiCategoryDO = new PiiCategoryDO();
                        piiCategoryDO.setPiiCatId(resultSet.getInt(1));
                        piiCategoryDO.setPiiCat(resultSet.getString(2));
                        piiCategoryDO.setPiiCatDescription(resultSet.getString(3));
                        piiCategoryDO.setSensitivity(resultSet.getInt(4));
                        personallyIdentifiableInfoCatList.add(piiCategoryDO);
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode(), e);
                }
            } catch (SQLException e) {
                log.error("Database error. Could not get the personally identifiable info categories.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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

        PiiCategoryDO piiCategory;
        Savepoint savepoint;
        String deletePurposeMapQuery = SQLQueries.DELETE_PII_MAPPINGS_TO_PURPOSE;
        String deleteQuery = SQLQueries.DELETE_PII_CAT;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement deletePurposeMapPrepStat = connection.prepareStatement(deletePurposeMapQuery);
                 PreparedStatement deletePreparedStatement = connection.prepareStatement(deleteQuery)) {
                piiCategory = getPIICatById(connection, categoryId);

                deletePurposeMapPrepStat.setInt(1, categoryId);
                deletePurposeMapPrepStat.executeUpdate();

                deletePreparedStatement.setInt(1, categoryId);
                deletePreparedStatement.executeUpdate();
                connection.commit();
                log.info("Successfully deleted the personally identifiable category from the database.");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not delete the personally identifiable info category.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PII_CATEGORY_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PII_CATEGORY_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            return getPIICatById(connection, catId);
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
    }

    private PiiCategoryDO getPIICatById(Connection con, int catId) throws DataAccessException {

        PiiCategoryDO piiCategory = new PiiCategoryDO();
        String selectQuery = SQLQueries.GET_PII_CAT_BY_ID;

        try (PreparedStatement selectPrepStat = con.prepareStatement(selectQuery)) {
            selectPrepStat.setInt(1, catId);
            try (ResultSet resultSet = selectPrepStat.executeQuery()) {
                resultSet.first();
                piiCategory.setPiiCatId(resultSet.getInt(1));
                piiCategory.setPiiCat(resultSet.getString(2));
                piiCategory.setPiiCatDescription(resultSet.getString(3));
                piiCategory.setSensitivity(resultSet.getInt(4));
            } catch (SQLException e) {
                log.error("Database error. Could not get the details of the personally identifiable info category.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_BY_ID_FAILURE
                                .getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PII_CATEGORY_BY_ID_FAILURE.getErrorCode(),
                        e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.GET_PURPOSES;
        Savepoint savepoint;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                        purposeDetailsDO.setThirdPartyName(resultSet.getString(7));

                        PiiCategoryDO[] piiCategoryDOArr = getPersonalIdentifyCatForPurposeConf(connection, resultSet
                                .getInt(1)).toArray(new PiiCategoryDO[0]);
                        purposeDetailsDO.setpiiCategoryArr(piiCategoryDOArr);
                        purposeDetailsDOList.add(purposeDetailsDO);
                    }
                    connection.commit();
                } catch (SQLException | DataAccessException e) {
                    connection.rollback(savepoint);
                    log.error("Database error. Could not get purpose details for config.", e);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSES_FAILURE.getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSES_FAILURE.getErrorCode(), e);
                }
            } catch (SQLException e) {
                connection.rollback(savepoint);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
        return purposeDetailsDOList;
    }

    private List<PurposeCategoryDO> getPurposeCatsForPurposeConf(Connection connection, int purposeId) throws
            DataAccessException {

        String query = SQLQueries.PURPOSE_CATS_FOR_PURPOSE_CONF_QUERY;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, purposeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
        }
    }

    private List<PiiCategoryDO> getPersonalIdentifyCatForPurposeConf(Connection connection, int purposeId) throws
            DataAccessException {

        String query = SQLQueries.PERSONALLY_IDENTIFIABLE_CAT_FOR_PURPOSE_CONF_QUERY;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, purposeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.ADD_PURPOSE;
        String purposeIdQuery = SQLQueries.SELECT_PURPOSE_ID_QUERY;
        Savepoint savepoint;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 PreparedStatement purposeIdPrepStat = connection.prepareStatement(purposeIdQuery)) {
                preparedStatement.setString(1, purpose.getPurpose());
                preparedStatement.setString(2, purpose.getPrimaryPurpose());
                preparedStatement.setString(3, purpose.getTermination());
                preparedStatement.setString(4, purpose.getThirdPartyDis());
                preparedStatement.setInt(5, purpose.getThirdPartyId());
                preparedStatement.executeUpdate();

                purposeIdPrepStat.setString(1, purpose.getPurpose());
                try (ResultSet resultSet = purposeIdPrepStat.executeQuery()) {
                    resultSet.first();
                    mapPurposeWithPurposeCategories(connection, resultSet.getInt(1), purpose.getPurposeCategoryDOArr());
                    mapPurposeWithPersonalInfoCategories(connection, resultSet.getInt(1), purpose.getpiiCategoryArr());
                    purposeDetails = getPurposeDetailsById(resultSet.getInt(1));
                }
                connection.commit();
                log.info("Successfully added the purpose details to the database");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not add purpose details.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PURPOSE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PURPOSE_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
        return purposeDetails;
    }

    private void mapPurposeWithPurposeCategories(Connection connection, int purposeId, PurposeCategoryDO[]
            purposeCategories) throws DataAccessException {

        String query = SQLQueries.ADD_PURPOSE_MAPPING_WITH_PURPOSE_CAT;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (PurposeCategoryDO purposeCategory : purposeCategories) {
                preparedStatement.setInt(1, purposeId);
                preparedStatement.setInt(2, purposeCategory.getPurposeCatId());
                preparedStatement.setInt(3, purposeCategory.getPurposeCatId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Database error. Could not map purpose to purpose category.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_MAP_PURPOSE_AND_PURPOSE_CAT_FAILURE
                            .getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_MAP_PURPOSE_AND_PURPOSE_CAT_FAILURE.getErrorCode
                            (), e);
        }
    }

    private void mapPurposeWithPersonalInfoCategories(Connection connection, int purposeId, PiiCategoryDO[]
            piiCategories) throws DataAccessException {

        String query = SQLQueries.ADD_PURPOSE_MAPPING_WITH_PII_CATEGORY;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (PiiCategoryDO piiCategory : piiCategories) {
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
        Savepoint savepoint;
        String query = SQLQueries.PURPOSE_DETAILS_UPDATE_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement updatePrepStat = connection.prepareStatement(query)) {
                updatePrepStat.setString(1, purpose.getPurpose());
                updatePrepStat.setString(2, purpose.getPrimaryPurpose());
                updatePrepStat.setString(3, purpose.getTermination());
                updatePrepStat.setString(4, purpose.getThirdPartyDis());
                updatePrepStat.setInt(5, purpose.getThirdPartyId());
                updatePrepStat.setInt(6, purpose.getPurposeId());
                updatePrepStat.executeUpdate();

                deleteMappingsWithPurpose(connection, purpose.getPurposeId());
                mapPurposeWithPurposeCategories(connection, purpose.getPurposeId(), purpose.getPurposeCategoryDOArr());
                mapPurposeWithPersonalInfoCategories(connection, purpose.getPurposeId(), purpose.getpiiCategoryArr());
                connection.commit();
                purposeDetailsDO = getPurposeDetailsById(purpose.getPurposeId());
                log.info("Successfully updated the purpose details to the database.");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not update purpose details.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PURPOSE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PURPOSE_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
        return purposeDetailsDO;
    }

    private void deleteMappingsWithPurpose(Connection connection, int purposeId) throws DataAccessException {

        deletePurposeCatMapWithPurpose(connection, purposeId);
        deletePersonalInfoCatMapWithPurpose(connection, purposeId);
    }

    private void deletePurposeCatMapWithPurpose(Connection connection, int purposeId) throws DataAccessException {

        String deletePurposeCat = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_PURPOSE_CAT;
        try (PreparedStatement deletePurposeCatStat = connection.prepareStatement(deletePurposeCat)) {
            deletePurposeCatStat.setInt(1, purposeId);
            deletePurposeCatStat.executeUpdate();
            deletePersonalInfoCatMapWithPurpose(connection, purposeId);
        } catch (SQLException | DataAccessException e) {
            log.error("Database error. Could not delete purpose map with purpose categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_CAT_MAP_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_CAT_MAP_FAILURE.getErrorCode(), e);
        }
    }

    private void deletePersonalInfoCatMapWithPurpose(Connection connection, int purposeId) throws DataAccessException {

        String deletePersonalInfoCat = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_PII_CAT;
        try (PreparedStatement deletePersonalInfoCatStat = connection.prepareStatement(deletePersonalInfoCat)) {
            deletePersonalInfoCatStat.setInt(1, purposeId);
            deletePersonalInfoCatStat.executeUpdate();
        } catch (SQLException e) {
            log.error("Database error. Could not delete purpose map with personally identifiable categories.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_PII_CAT_MAP_FAILURE
                            .getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_PII_CAT_MAP_FAILURE.getErrorCode
                            (), e);
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

        PurposeDetailsDO deletedPurpose;
        Savepoint savepoint;
        String deletePIIMapQuery = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_PII_CAT;
        String deletePurposeCatMapQuery = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_PURPOSE_CAT;
        String deleteServiceMapQuery = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_SERVICE;
        String deleteConsentMapQuery = SQLQueries.DELETE_PURPOSE_MAPPINGS_TO_CONSENT;
        String deleteQuery = SQLQueries.DELETE_PURPOSE;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement deletePIIMapPrepStat = connection.prepareStatement(deletePIIMapQuery);
                 PreparedStatement deletePurposeCatMapPrepStat = connection.prepareStatement
                         (deletePurposeCatMapQuery);
                 PreparedStatement deleteServiceMapPrepStat = connection.prepareStatement(deleteServiceMapQuery);
                 PreparedStatement deleteConsentMapPrepStat = connection.prepareStatement(deleteConsentMapQuery);
                 PreparedStatement deletePreparedStatement = connection.prepareStatement(deleteQuery)) {
                deletedPurpose = getPurposeById(connection, purposeId);

                deletePIIMapPrepStat.setInt(1, purposeId);
                deletePIIMapPrepStat.executeUpdate();

                deletePurposeCatMapPrepStat.setInt(1, purposeId);
                deletePurposeCatMapPrepStat.executeUpdate();

                deleteServiceMapPrepStat.setInt(1, purposeId);
                deleteServiceMapPrepStat.executeUpdate();

                deleteConsentMapPrepStat.setInt(1, purposeId);
                deleteConsentMapPrepStat.executeUpdate();

                deletePreparedStatement.setInt(1, purposeId);
                deletePreparedStatement.executeUpdate();
                connection.commit();
                log.info("Successfully deleted the purpose : " + deletedPurpose.getPurpose() + " from the database");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not delete the purpose.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
        return deletedPurpose;
    }

    /**
     * Get details of a purpose by purpose id.
     *
     * @param id of a purpose.
     * @return details of the purpose.
     */
    @Override
    public PurposeDetailsDO getPurposeDetailsById(int id) throws DataAccessException {

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            return getPurposeById(connection, id);
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
    }

    private PurposeDetailsDO getPurposeById(Connection connection, int id) throws DataAccessException {

        PurposeDetailsDO purpose = new PurposeDetailsDO();
        String selectQuery = SQLQueries.SELECT_PURPOSE_DETAILS_BY_ID_QUERY;

        try (PreparedStatement selectPrepStat = connection.prepareStatement(selectQuery)) {
            selectPrepStat.setInt(1, id);
            try (ResultSet resultSet = selectPrepStat.executeQuery()) {
                resultSet.first();
                purpose.setPurposeId(resultSet.getInt(1));
                purpose.setPurpose(resultSet.getString(2));
                purpose.setPrimaryPurpose(resultSet.getString(3));
                purpose.setTermination(resultSet.getString(4));
                purpose.setThirdPartyDis(resultSet.getString(5));
                purpose.setThirdPartyId(resultSet.getInt(6));
                purpose.setThirdPartyName(resultSet.getString(7));

                PurposeCategoryDO[] purposeCategoryDOS = getPurposeCatsForPurposeConf(connection, id).toArray(new
                        PurposeCategoryDO[0]);
                purpose.setPurposeCategoryDOArr(purposeCategoryDOS);

                PiiCategoryDO[] piiCategoryDOS = getPersonalIdentifyCatForPurposeConf(connection, id).toArray(new
                        PiiCategoryDO[0]);
                purpose.setpiiCategoryArr(piiCategoryDOS);
            } catch (SQLException | DataAccessException e) {
                log.error("Database error. Could not get details of the purpose.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
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
        Savepoint savepoint;
        String query = SQLQueries.ADD_SERVICE;
        String selectIdQuery = SQLQueries.SELECT_LAST_INSERTED_SERVICE_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 PreparedStatement selectIdPrepStat = connection.prepareStatement(selectIdQuery)) {
                try (ResultSet resultSet = selectIdPrepStat.executeQuery()) {
                    preparedStatement.setString(1, service.getServiceDescription());
                    preparedStatement.executeUpdate();
                    resultSet.first();
                    mapServiceWithPurposes(connection, resultSet.getInt(1), service);
                    servicesDO = getService(connection, resultSet.getInt(1));
                    connection.commit();
                    log.info("Successfully added service details to the database");
                } catch (SQLException | DataAccessException e) {
                    connection.rollback(savepoint);
                    log.error("Database error. Could not add service details to the database.", e);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_SERVICE_FAILURE.getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_SERVICE_FAILURE.getErrorCode(), e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
        return servicesDO;
    }

    private void mapServiceWithPurposes(Connection connection, int serviceId, ServicesDO services) throws
            DataAccessException {

        String query = SQLQueries.ADD_SERVICE_MAPPINGS_TO_PURPOSE_QUERY;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (PurposeDetailsDO purpose : services.getPurposeDetailsArr()) {
                preparedStatement.setInt(1, serviceId);
                preparedStatement.setInt(2, purpose.getPurposeId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Database error. Could not map service with purposes.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_SERVICE_MAP_PURPOSE_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_SERVICE_MAP_PURPOSE_FAILURE.getErrorCode(), e);
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
        Savepoint savepoint;
        String query = SQLQueries.UPDATE_SERVICE;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, service.getServiceDescription());
                preparedStatement.setInt(2, service.getServiceId());
                preparedStatement.executeUpdate();
                deleteServiceWithPurposesMap(connection, service.getServiceId());
                mapServiceWithPurposes(connection, service.getServiceId(), service);
                updatedServiceDO = getService(connection, service.getServiceId());
                connection.commit();
                log.info("Successfully updated the service details to the database");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not update service details.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_SERVICE_ERROR.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_SERVICE_ERROR.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
        return updatedServiceDO;
    }

    private void deleteServiceWithPurposesMap(Connection connection, int serviceId) throws DataAccessException {

        String query = SQLQueries.DELETE_SERVICE_MAPPING_TO_PURPOSE_QUERY;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, serviceId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Database error. Could not delete the service mapping with purposes.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_SERVICE_MAP_PURPOSE_FAILURE
                            .getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_SERVICE_MAP_PURPOSE_FAILURE.getErrorCode
                            (), e);
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

        String query = SQLQueries.SERVICES_FOR_CONF_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<ServicesDO> servicesDOList = new ArrayList<>();
                    while (resultSet.next()) {
                        ServicesDO servicesDO = new ServicesDO();
                        servicesDO.setServiceId(resultSet.getInt(1));
                        servicesDO.setServiceDescription(resultSet.getString(2));
                        PurposeDetailsDO[] purposeDetailsDO =
                                getPurposeDetailsForServiceConf(connection, resultSet.getInt
                                        (1)).toArray(new PurposeDetailsDO[0]);
                        servicesDO.setPurposeDetails(purposeDetailsDO);
                        servicesDOList.add(servicesDO);
                    }
                    return servicesDOList;
                } catch (SQLException e) {
                    log.error("Database error. Could not get services for config.", e);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICES_FAILURE.getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICES_FAILURE.getErrorCode(), e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
    }

    private List<PurposeDetailsDO> getPurposeDetailsForServiceConf(Connection connection, int serviceId) throws
            DataAccessException {

        String query = SQLQueries.PURPOSE_DETAILS_FOR_SERVICE_CONF_QUERY;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, serviceId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FOR_SERVICE_FAILURE
                                .getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_FOR_SERVICE_FAILURE.getErrorCode(),
                        e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
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

        ServicesDO deletedService;
        Savepoint savepoint;
        String deletePurposeMapQuery = SQLQueries.DELETE_SERVICE_MAPPING_TO_PURPOSE_QUERY;
        String deleteUserMapQuery = SQLQueries.DELETE_SERVICE_MAPPING_TO_USER;
        String deleteQuery = SQLQueries.DELETE_SERVICE;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement deletePurposeMapPrepStat = connection.prepareStatement(deletePurposeMapQuery);
                 PreparedStatement deleteUserMapPrepStat = connection.prepareStatement(deleteUserMapQuery);
                 PreparedStatement deletePreparedStatement = connection.prepareStatement(deleteQuery)) {

                deletedService = getService(connection, serviceId);
                deletePurposeMapPrepStat.setInt(1, serviceId);
                deletePurposeMapPrepStat.executeUpdate();

                deleteUserMapPrepStat.setInt(1, serviceId);
                deleteUserMapPrepStat.executeUpdate();

                deletePreparedStatement.setInt(1, serviceId);
                deletePreparedStatement.executeUpdate();
                connection.commit();
                log.info("Successfully deleted the service : " + deletedService.getServiceDescription());
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not delete the service.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_SERVICE_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_SERVICE_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
        return deletedService;
    }

    /**
     * This methods returns the service details by a service id.
     *
     * @param id of a service to get the service details.
     * @return details of the relevant service for the id.
     */
    @Override
    public ServicesDO getServiceById(int id) throws DataAccessException {

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            return getService(connection, id);
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
    }

    private ServicesDO getService(Connection connection, int id) throws DataAccessException {

        ServicesDO servicesDO = new ServicesDO();
        String selectQuery = SQLQueries.GET_SERVICES_BY_ID_QUERY;

        try (PreparedStatement selectPrepStat = connection.prepareStatement(selectQuery)) {
            selectPrepStat.setInt(1, id);
            try (ResultSet resultSet = selectPrepStat.executeQuery()) {
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
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.CONSENT_BY_USER_REVOKE_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, currentTime);
                preparedStatement.setString(2, consentDO.getSGUID());
                for (ServicesDO revokeServices : serviceList) {
                    preparedStatement.setInt(3, revokeServices.getServiceId());
                    for (int j = 0; j < revokeServices.getPurposeDetailsArr().length; j++) {
                        if (revokeServices.getPurposeDetailsArr()[j].getPurposeId() != 0) {
                            preparedStatement.setInt(4, revokeServices.getPurposeDetailsArr()[j].
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
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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

        String query = SQLQueries.SERVICES_FOR_USER_VIEW_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, subjectName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                } catch (SQLException | DataAccessException e) {
                    log.error("Database error. Could not get services.", e);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICES_FAILURE.getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_SERVICES_FAILURE.getErrorCode(), e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
    }

    private List<Integer> getPurposeIdByUserByService(Connection connection, String sguid, int serviceId) throws
            DataAccessException {

        String query = SQLQueries.PURPOSE_ID_BY_USER_BY_SERVICE_QUERY;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setString(2, sguid);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.SELECT_PURPOSE_CATEGORIES_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATEGORIES_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CATEGORIES_FAILURE
                                    .getErrorCode(),
                            e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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
        Savepoint savepoint;
        String query = SQLQueries.ADD_PURPOSE_CATEGORY;
        String selectIdQuery = SQLQueries.SELECT_LAST_INSERTED_PURPOSE_CAT;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 PreparedStatement selectIdPrepStat = connection.prepareStatement(selectIdQuery)) {
                preparedStatement.setString(1, purposeCategory.getPurposeCatShortCode());
                preparedStatement.setString(2, purposeCategory.getPurposeCatDes());
                preparedStatement.executeUpdate();

                try (ResultSet resultSet = selectIdPrepStat.executeQuery()) {
                    resultSet.first();
                    addedPurposeCatDO = getPurposeCategory(connection, resultSet.getInt(1));
                } catch (SQLException | DataAccessException e) {
                    connection.rollback(savepoint);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode(), e);
                }
                connection.commit();
                log.info("Successfully added the Purpose Category to the database.");
            } catch (SQLException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not add purpose category.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PURPOSE_CAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_PURPOSE_CAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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
        Savepoint savepoint;
        String query = SQLQueries.PURPOSE_CATEGORY_UPDATE_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, purposeCategory.getPurposeCatShortCode());
                preparedStatement.setString(2, purposeCategory.getPurposeCatDes());
                preparedStatement.setInt(3, purposeCategory.getPurposeCatId());
                preparedStatement.executeUpdate();
                updatedPurposeCatDO = getPurposeCategory(connection, purposeCategory.getPurposeCatId());
                connection.commit();
                log.info("Successfully updated the Purpose Category to the database");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not update purpose category.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PURPOSE_CAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_PURPOSE_CAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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

        PurposeCategoryDO purposeCategory;
        Savepoint savepoint;
        String deleteMappingsQuery = SQLQueries.DELETE_PURPOSE_CATEGORY_MAPPINGS_QUERY;
        String deleteQuery = SQLQueries.DELETE_PURPOSE_CATEGORY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement deleteMapsPrepStat = connection.prepareStatement(deleteMappingsQuery);
                 PreparedStatement deletePrepStat = connection.prepareStatement(deleteQuery)) {
                purposeCategory = getPurposeCategory(connection, categoryId);

                deleteMapsPrepStat.setInt(1, categoryId);
                deleteMapsPrepStat.executeUpdate();

                deletePrepStat.setInt(1, categoryId);
                deletePrepStat.executeUpdate();
                connection.commit();
                log.info("Successfully deleted the Purpose Category from the database");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not delete purpose category.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_CAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_DELETE_PURPOSE_CAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            return getPurposeCategory(connection, id);
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
        }
    }

    private PurposeCategoryDO getPurposeCategory(Connection connection, int id) throws DataAccessException {

        PurposeCategoryDO purposeCategoryDO = new PurposeCategoryDO();
        String selectQuery = SQLQueries.GET_PURPOSE_CATEGORY_BY_ID;

        try (PreparedStatement selectPrepStat = connection.prepareStatement(selectQuery)) {
            selectPrepStat.setInt(1, id);
            try (ResultSet resultSet = selectPrepStat.executeQuery()) {
                resultSet.first();
                purposeCategoryDO.setPurposeCatId(resultSet.getInt(1));
                purposeCategoryDO.setPurposeCatShortCode(resultSet.getString(2));
                purposeCategoryDO.setPurposeCatDes(resultSet.getString(3));
            } catch (SQLException e) {
                log.error("Database error. Could not get the purpose category details.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_PURPOSE_CAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
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
        String query = SQLQueries.SELECT_THIRD_PARTIES_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        ThirdPartyDO thirdParty = new ThirdPartyDO();
                        thirdParty.setThirdPartyId(resultSet.getInt(1));
                        thirdParty.setThirdPartyName(resultSet.getString(2));
                        thirdPartyList.add(thirdParty);
                    }
                } catch (SQLException e) {
                    log.error("Database error. Could not get third party details.", e);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTIES_FAILURE
                                    .getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTIES_FAILURE.getErrorCode(),
                            e);
                }
            } catch (SQLException e) {
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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
        Savepoint savepoint;
        String query = SQLQueries.ADD_THIRD_PARTY_QUERY;
        String selectIdQuery = SQLQueries.SELECT_LAST_INSERTED_THIRD_PARTY_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 PreparedStatement selectIdPrepStat = connection.prepareStatement(selectIdQuery)) {
                preparedStatement.setString(1, thirdParty.getThirdPartyName());
                preparedStatement.executeUpdate();

                try (ResultSet resultSet = selectIdPrepStat.executeQuery()) {
                    resultSet.first();
                    addedThirdPartyDO = getThirdParty(connection, resultSet.getInt(1));
                } catch (SQLException | DataAccessException e) {
                    connection.rollback(savepoint);
                    throw new DataAccessException(
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorMessage(),
                            ConsentReceiptConstants.ErrorMessages.ERROR_CODE_RESULT_SET_FAILURE.getErrorCode(), e);
                }
                connection.commit();
                log.info("Successfully added the Third Party to the database.");
            } catch (SQLException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not add the third party details.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_THIRD_PARTY_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_ADD_THIRD_PARTY_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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
        Savepoint savepoint;
        String query = SQLQueries.UPDATE_THIRD_PARTY_QUERY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, thirdParty.getThirdPartyName());
                preparedStatement.setInt(2, thirdParty.getThirdPartyId());
                preparedStatement.executeUpdate();
                updatedThirdPartyDO = getThirdParty(connection, thirdParty.getThirdPartyId());
                log.info("Successfully updated the Third Party to the database");
            } catch (SQLException | DataAccessException e) {
                connection.rollback(savepoint);
                log.error("Database error. Could not update the third party.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_THIRD_PARTY_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_UPDATE_THIRD_PARTY_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_CONNECTION_FAILURE.getErrorCode(), e);
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

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            return getThirdParty(connection, id);
        } catch (SQLException e) {
            log.error("Database error. Could not get the details of the Third Party.", e);
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTY_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTY_FAILURE.getErrorCode(), e);
        }
    }

    private ThirdPartyDO getThirdParty(Connection connection, int id) throws DataAccessException {

        ThirdPartyDO thirdPartyDO = new ThirdPartyDO();
        String selectQuery = SQLQueries.GET_THIRD_PARTY_BY_ID_QUERY;

        try (PreparedStatement selectPrepStat = connection.prepareStatement(selectQuery)) {
            selectPrepStat.setInt(1, id);
            try (ResultSet resultSet = selectPrepStat.executeQuery()) {
                resultSet.first();
                thirdPartyDO.setThirdPartyId(resultSet.getInt(1));
                thirdPartyDO.setThirdPartyName(resultSet.getString(2));
            } catch (SQLException e) {
                log.error("Database error. Could not get the details of the Third Party.", e);
                throw new DataAccessException(
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTY_FAILURE.getErrorMessage(),
                        ConsentReceiptConstants.ErrorMessages.ERROR_CODE_GET_THIRD_PARTY_FAILURE.getErrorCode(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_PREP_STAT_FAILURE.getErrorCode(), e);
        }
        return thirdPartyDO;
    }
}
