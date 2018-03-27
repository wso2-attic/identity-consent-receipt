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
 * Class contains the constants that were used in this module.
 */
public class ConsentReceiptConstants {

    public static final String VERSION = "version";
    public static final String VERSION_NUMBER = "KI-CR-v1.0.0";
    public static final String JURISDICTION = "jurisdiction";
    public static final String CONSENT_TIMESTAMP = "consentTimestamp";
    public static final String COLLECTION_METHOD = "collectionMethod";
    public static final String CONSENT_RECEIPT_ID = "consentReceiptID";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String SUBJECT = "subject";
    public static final String DATA_CONTROLLER = "dataController";
    public static final String ORGANIZATION_NAME = "org";
    public static final String CONTACT_NAME = "contact";
    public static final String ADDRESS = "address";
    public static final String STREET = "streetAddress";
    public static final String COUNTRY = "addressCountry";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String POLICY_URL = "policyUrl";
    public static final String SERVICES = "services";
    public static final String SERVICE_NAME = "serviceName";
    public static final String PURPOSES = "purposes";
    public static final String PURPOSE = "purpose";
    public static final String PURPOSE_CATEGORY = "purposeCategory";
    public static final String CONSENT_TYPE = "consentType";
    public static final String PERSONALLY_IDENTIFIABLE_INFO_CATEGORY = "piiCategory";
    public static final String PRIMARY_PURPOSE = "primaryPurpose";
    public static final String TERMINATION = "termination";
    public static final String THIRD_PARTY_DISCLOSURE = "thirdPartyDisclosure";
    public static final String THIRD_PARTY_NAME = "thirdPartyName";
    public static final String SENSITIVE = "sensitive";
    public static final String SENSITIVE_PERSONAL_INFO_CATEGORY = "spiCat";

    /**
     * Error messages for the consent receipt spec development.
     */
    public enum ErrorMessages {
        ERROR_CODE_GET_USER_AND_DATA_CONTROLLER_FAILURE("CM_10000", "Database error. Could not get the user and data" +
                " controller details."),
        ERROR_CODE_GET_USER_CONSENT_FAILURE("CM_10001", "Database error. Could not get consent details for the user."),
        ERROR_CODE_GET_USER_CONSENT_FOR_THIRD_PARTY_FAILURE("CM_10002", "Database error. Could not get consent " +
                "details of the user to third party."),
        ERROR_CODE_GET_PURPOSE_IDS_FOR_SERVICES_FAILURE("CM_10003", "Database error. Could not get purpose IDs " +
                "for services."),
        ERROR_CODE_GET_INTERNAL_ID_FAILURE("CM_10004", "Database error. Could not get internal ID for the user."),
        ERROR_CODE_ADD_USER_CONSENT_FAILURE("CM_10005", "Database error. Could not add the user consents to the" +
                " database."),
        ERROR_CODE_GET_TERMINATION_FAILURE("CM_10006", "Database error. Could not get purpose termination days."),
        ERROR_CODE_DATE_PARSE_FAILURE("CM_10007", "Error occurred while parsing the consented date."),
        ERROR_CODE_GET_CONSENT_SERVICES_FAILURE("CM_10008", "Database error. Could not get services for the user."),
        ERROR_CODE_GET_PURPOSE_FAILURE("CM_10009", "Database error. Could not get purpose."),
        ERROR_CODE_GET_PURPOSE_FOR_USER_FAILURE("CM_10010", "Database error. Could not get purpose details for the " +
                "user by service."),
        ERROR_CODE_USER_NAME_EXISTS_FAILURE("CM_10011", "Database error. Could not get the existence of the user " +
                "in the database."),
        ERROR_CODE_DATA_CONTROLLER_EXISTS_FAILURE("CM_10012", "Database error. Could not get the existence of the " +
                "data controller."),
        ERROR_CODE_GET_SERVICE_ID_FAILURE("CM_10013", "Database error. Could not get service ID."),
        ERROR_CODE_GET_PURPOSE_ID_FAILURE("CM_10014", "Database error. Could not get purpose ID."),
        ERROR_CODE_GET_SENSITIVE_PII_CATEGORIES_FAILURE("CM_10015", "Database error. Could not get the sensitive " +
                "personal info categories."),
        ERROR_CODE_GET_PURPOSE_CATEGORIES_FAILURE("CM_10016", "Database error. Could not get purpose categories."),
        ERROR_CODE_GET_DATA_CONTROLLER_FAILURE("CM_10017", "Database error. Could not get the data controller " +
                "details."),
        ERROR_CODE_GET_DATA_CONTROLLERS_FAILURE("CM_10018", "Database error. Could not get data controllers' details."),
        ERROR_CODE_ADD_DATA_CONTROLLER_FAILURE("CM_10019", "Database error. Could not add data controller details " +
                "to the database."),
        ERROR_CODE_UPDATE_DATA_CONTROLLER_FAILURE("CM_10020", "Database error. Could not update the data controller."),
        ERROR_CODE_DELETE_DATA_CONTROLLER_FAILURE("CM_10021", "Database error. Could not delete data controller."),
        ERROR_CODE_ADD_PII_CATEGORY_FAILURE("CM_10022", "Database error. Could not add personally identifiable info" +
                " category to the database."),
        ERROR_CODE_UPDATE_PII_CATEGORY_FAILURE("CM_10023", "Database error. Could not update personally identifiable" +
                " info category."),
        ERROR_CODE_GET_PII_CATEGORY_FAILURE("CM_10024", "Database error. Could not get the personally identifiable" +
                " info categories."),
        ERROR_CODE_DELETE_PII_CATEGORY_FAILURE("CM_10025", "Database error. Could not delete the personally" +
                " identifiable info category."),
        ERROR_CODE_GET_PII_CATEGORY_BY_ID_FAILURE("CM_10026", "Database error. Could not get the details of the " +
                "personally identifiable info category."),
        ERROR_CODE_GET_PURPOSES_FAILURE("CM_10027", "Database error. Could not get purpose details for config."),
        ERROR_CODE_GET_PURPOSE_CATS_FAILURE("CM_10028", "Database error. Could not get purpose categories."),
        ERROR_CODE_ADD_PURPOSE_FAILURE("CM_10029", "Database error. Could not add purpose details."),
        ERROR_CODE_MAP_PURPOSE_AND_PURPOSE_CAT_FAILURE("CM_10030", "Database error. Could not map purpose to purpose" +
                " category."),
        ERROR_CODE_MAP_PURPOSE_AND_PII_CAT_FAILURE("CM_10031", "Database error. Could not map purpose to personally" +
                " identifiable info category."),
        ERROR_CODE_UPDATE_PURPOSE_FAILURE("CM_10032", "Database error. Could not update purpose details."),
        ERROR_CODE_DELETE_PURPOSE_CAT_MAP_FAILURE("CM_10033", "Database error. Could not delete purpose map with " +
                "purpose categories."),
        ERROR_CODE_DELETE_PURPOSE_PII_CAT_MAP_FAILURE("CM_10034", "Database error. Could not delete purpose map with" +
                " personally identifiable categories."),
        ERROR_CODE_DELETE_PURPOSE_FAILURE("CM_10035", "Database error. Could not delete the purpose."),
        ERROR_CODE_ADD_SERVICE_FAILURE("CM_10036", "Database error. Could not add service details to the database."),
        ERROR_CODE_SERVICE_MAP_PURPOSE_FAILURE("CM_10037", "Database error. Could not map service with purposes."),
        ERROR_CODE_UPDATE_SERVICE_ERROR("CM_10038", "Database error. Could not update service details."),
        ERROR_CODE_DELETE_SERVICE_MAP_PURPOSE_FAILURE("CM_10039", "Database error. Could not delete the service " +
                "mapping with purposes."),
        ERROR_CODE_GET_SERVICES_FAILURE("CM_10040", "Database error. Could not get services for config."),
        ERROR_CODE_GET_PURPOSE_FOR_SERVICE_FAILURE("CM_10041", "Database error. Could not get purpose details for" +
                " service config."),
        ERROR_CODE_DELETE_SERVICE_FAILURE("CM_10042", "Database error. Could not delete the service."),
        ERROR_CODE_GET_SERVICE_FAILURE("CM_10043", "Database error. Could not get the service details."),
        ERROR_CODE_REVOKE_CONSENT_FAILURE("CM_10044", "Database error. Could not revoke consent."),
        ERROR_CODE_ADD_PURPOSE_CAT_FAILURE("CM_10045", "Database error. Colud not add purpose category."),
        ERROR_CODE_UPDATE_PURPOSE_CAT_FAILURE("CM_10046", "Database error. Could not update purpose category."),
        ERROR_CODE_DELETE_PURPOSE_CAT_FAILURE("CM_10047", "Database error. Could not delete purpose category."),
        ERROR_CODE_GET_PURPOSE_CAT_FAILURE("CM_10048", "Database error. Could not get the purpose category details."),
        ERROR_CODE_GET_THIRD_PARTIES_FAILURE("CM_10049", "Database error. Could not get third party details."),
        ERROR_CODE_ADD_THIRD_PARTY_FAILURE("CM_10050", "Database error. Could not add the third party details."),
        ERROR_CODE_UPDATE_THIRD_PARTY_FAILURE("CM_10051", "Database error. Could not update the third party."),
        ERROR_CODE_GET_THIRD_PARTY_FAILURE("CM_10052", "Database error. Could not get the details of the Third Party."),
        ERROR_CODE_NO_APPROVED_CONSENTS("CM_10053", "You have not given any approved consents."),
        ERROR_CODE_PREP_STAT_FAILURE("CM_10054", "Error occurred while creating the prepared statement."),
        ERROR_CODE_RESULT_SET_FAILURE("CM_10055", "Error occurred while creating the result set."),
        ERROR_CODE_CONNECTION_FAILURE("CM_10056", "Error occurred in the connection.");

        final String errorCode;
        final String errorMessage;

        ErrorMessages(String errorCode, String errorMessage) {

            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getErrorCode() {

            return errorCode;
        }

        public String getErrorMessage() {

            return errorMessage;
        }
    }
}
