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

package org.wso2.identity.carbon.user.consent.mgt.backend.jsonparser;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.wso2.identity.carbon.user.consent.mgt.backend.constants.ConsentReceiptConstants;
import org.wso2.identity.carbon.user.consent.mgt.backend.dao.impl.ConsentDaoImpl;
import org.wso2.identity.carbon.user.consent.mgt.backend.exception.DataAccessException;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.*;
import org.wso2.identity.carbon.user.consent.mgt.backend.utils.DateTimeUtil;
import org.wso2.identity.carbon.user.consent.mgt.backend.utils.UniqueIdUtil;

import java.util.List;

/**
 * Class that read or create the consent receipt string.
 */
public class JsonParser {

    /**
     * Creates the consent receipt for the user.
     *
     * @param piiPrincipalId username of an user.
     * @return a JSONObject that contains the consents of the above user.
     */
    public JSONObject createConsentReceipt(String piiPrincipalId) throws DataAccessException {

        ConsentDaoImpl consentDaoImpl = new ConsentDaoImpl(piiPrincipalId);
        List<ConsentDO> consentDOList = consentDaoImpl.getUserConsent();
        ConsentDO consentDO = consentDOList.get(0);

        JSONObject jsonObject = new JSONObject();

        jsonObject = setUserAndDataControllerDetails(jsonObject, consentDO);

        consentDaoImpl = new ConsentDaoImpl(consentDO.getSGUID(), true);
        List<ServicesDO> servicesList = consentDaoImpl.getServices();
        if (servicesList.size() != 0) {
            List<PurposeDetailsDO> purposeCategoryDetailsList = consentDaoImpl.getPurposeCategories(consentDO
                    .getSGUID());
            String temp = "";
            JSONArray jsonServiceArr = new JSONArray();
            JSONObject[] jsonServiceObj = new JSONObject[servicesList.size()];
            for (int i = 0; i < servicesList.size(); i++) {
                ServicesDO service = servicesList.get(i);
                if (i == 0) {
                    jsonServiceObj[i] = new JSONObject();
                    temp = service.getServiceDescription();
                    jsonServiceObj[i].put(ConsentReceiptConstants.SERVICE_NAME, service.getServiceDescription());

                    JSONObject[] jsonPurposeObj = new JSONObject[servicesList.size()];
                    JSONArray jsonPurposeArr = new JSONArray();
                    String tempPurpose = "";
                    for (int j = 0; j < servicesList.size(); j++) {
                        PurposeDetailsDO purpose = servicesList.get(j).getPurposeDetails();
                        if (temp.equals(servicesList.get(j).getServiceDescription())) {
                            if (j == 0) {
                                jsonPurposeObj[j] = new JSONObject();
                                tempPurpose = purpose.getPurpose();

                                jsonPurposeObj[j] = setPurposeDetails(j, servicesList, temp, tempPurpose, purpose,
                                        purposeCategoryDetailsList, jsonPurposeObj[j]);

                            } else {
                                if (!tempPurpose.equals(purpose.getPurpose())) {
                                    jsonPurposeObj[j] = new JSONObject();
                                    tempPurpose = purpose.getPurpose();

                                    jsonPurposeObj[j] = setPurposeDetails(j, servicesList, temp, tempPurpose, purpose,
                                            purposeCategoryDetailsList, jsonPurposeObj[j]);
                                }
                            }
                            if (jsonPurposeObj[j] != null) {
                                jsonPurposeArr.add(jsonPurposeObj[j]);
                            }
                        } else {
                            break;
                        }
                    }
                    jsonServiceObj[i].put(ConsentReceiptConstants.PURPOSES, jsonPurposeArr);
                    jsonServiceArr.add(jsonServiceObj[i]);
                } else {
                    if (!temp.equals(service.getServiceDescription())) {
                        jsonServiceObj[i] = new JSONObject();
                        jsonServiceObj[i].put(ConsentReceiptConstants.SERVICE_NAME, service.getServiceDescription());
                        temp = service.getServiceDescription();

                        JSONObject[] jsonPurposeObj = new JSONObject[servicesList.size()];
                        JSONArray jsonPurposeArr = new JSONArray();
                        String tempPurpose = "";
                        for (int j = i; j < servicesList.size(); j++) {
                            PurposeDetailsDO purpose = servicesList.get(j).getPurposeDetails();
                            if (temp.equals(servicesList.get(j).getServiceDescription())) {
                                if (j == i) {
                                    jsonPurposeObj[j] = new JSONObject();
                                    tempPurpose = purpose.getPurpose();

                                    jsonPurposeObj[j] = setPurposeDetails(j, servicesList, temp, tempPurpose, purpose,
                                            purposeCategoryDetailsList, jsonPurposeObj[j]);
                                } else {
                                    if (!tempPurpose.equals(purpose.getPurpose())) {
                                        jsonPurposeObj[j] = new JSONObject();
                                        tempPurpose = purpose.getPurpose();

                                        jsonPurposeObj[j] = setPurposeDetails(j, servicesList, temp, tempPurpose,
                                                purpose,
                                                purposeCategoryDetailsList, jsonPurposeObj[j]);
                                    }
                                }
                                if (jsonPurposeObj[j] != null) {
                                    jsonPurposeArr.add(jsonPurposeObj[j]);
                                }
                            } else {
                                break;
                            }
                        }
                        jsonServiceObj[i].put(ConsentReceiptConstants.PURPOSES, jsonPurposeArr);
                        if (!jsonServiceObj[i].isEmpty()) {
                            jsonServiceArr.add(jsonServiceObj[i]);
                        }
                    }
                }
            }
            jsonObject.put(ConsentReceiptConstants.SERVICES, jsonServiceArr);

            List<PiiCategoryDO> piiCategoryList;
            piiCategoryList = consentDaoImpl.getSensitivePersonalInfoCategory(consentDO.getSGUID());
            jsonObject = setSensitivePersonalInfoCategory(jsonObject, piiCategoryList);
        } else {
            throw new DataAccessException(
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_NO_APPROVED_CONSENTS.getErrorMessage(),
                    ConsentReceiptConstants.ErrorMessages.ERROR_CODE_NO_APPROVED_CONSENTS.getErrorCode()
            );
        }
        return jsonObject;
    }

    private JSONObject setUserAndDataControllerDetails(JSONObject jsonObject, ConsentDO consentDO) {

        jsonObject.put(ConsentReceiptConstants.VERSION, ConsentReceiptConstants.VERSION_NUMBER);
        jsonObject.put(ConsentReceiptConstants.JURISDICTION, "DW"); //--This must be collected from the user's country
        jsonObject.put(ConsentReceiptConstants.CONSENT_TIMESTAMP, consentDO.getConsentTimestamp());
        jsonObject.put(ConsentReceiptConstants.COLLECTION_METHOD, consentDO.getCollectionMethod());
        jsonObject.put(ConsentReceiptConstants.CONSENT_RECEIPT_ID, UniqueIdUtil.createUniqueId());
        jsonObject.put(ConsentReceiptConstants.PUBLIC_KEY, consentDO.getDataController().getPublicKey());
        jsonObject.put(ConsentReceiptConstants.SUBJECT, consentDO.getPiiPrincipalId());
        jsonObject.put(ConsentReceiptConstants.POLICY_URL, consentDO.getDataController().getPolicyUrl());

        JSONObject jsonDataController = new JSONObject();
        jsonDataController.put(ConsentReceiptConstants.ORGANIZATION_NAME, consentDO.getDataController().getOrgName());
        jsonDataController.put(ConsentReceiptConstants.CONTACT_NAME, consentDO.getDataController().getContactName());

        JSONObject jsonAddress = new JSONObject();
        jsonAddress.put(ConsentReceiptConstants.STREET, consentDO.getDataController().getStreet());
        jsonAddress.put(ConsentReceiptConstants.COUNTRY, consentDO.getDataController().getCountry());
        jsonDataController.put(ConsentReceiptConstants.ADDRESS, jsonAddress);

        jsonDataController.put(ConsentReceiptConstants.EMAIL, consentDO.getDataController().getEmail());
        jsonDataController.put(ConsentReceiptConstants.PHONE, consentDO.getDataController().getPhoneNo());
        jsonObject.put(ConsentReceiptConstants.DATA_CONTROLLER, jsonDataController);

        return jsonObject;
    }

    private JSONObject setPurposeDetails(int j, List<ServicesDO> servicesList, String temp, String tempPurpose,
                                         PurposeDetailsDO purpose, List<PurposeDetailsDO> purposeCategoryDetailsList,
                                         JSONObject jsonPurposeObj) {

        JSONArray jsonPiiArr = new JSONArray();
        for (int k = j; k < servicesList.size(); k++) {
            if (temp.equals(servicesList.get(k).getServiceDescription())) {
                if (tempPurpose.equals(servicesList.get(k).getPurposeDetails().getPurpose())) {
                    jsonPiiArr.add(servicesList.get(k).getPurposeDetails().getPiiCategory().getPiiCat());
                } else {
                    break;
                }
            }
        }
        jsonPurposeObj.put(ConsentReceiptConstants.PERSONALLY_IDENTIFIABLE_INFO_CATEGORY, jsonPiiArr);
        jsonPurposeObj.put(ConsentReceiptConstants.PURPOSE, purpose.getPurpose());
        JSONArray jsonPurposeCatArr = new JSONArray();
        for (PurposeDetailsDO purposeDetails : purposeCategoryDetailsList) {
            if (purpose.getPurpose().equals(purposeDetails.getPurpose())) {
                jsonPurposeCatArr.add(purposeDetails.getPurposeCatId() + " - " + purposeDetails
                        .getPurposeCatShortCode());
            }
        }
        jsonPurposeObj.put(ConsentReceiptConstants.PURPOSE_CATEGORY, jsonPurposeCatArr);
        if (purpose.getPrimaryPurpose().equals("1")) {
            jsonPurposeObj.put(ConsentReceiptConstants.PRIMARY_PURPOSE, "Yes");
        } else {
            jsonPurposeObj.put(ConsentReceiptConstants.PRIMARY_PURPOSE, "No");
        }
        jsonPurposeObj.put(ConsentReceiptConstants.TERMINATION, purpose.getTermination());
        if (purpose.getThirdPartyDis().equals("1")) {
            jsonPurposeObj.put(ConsentReceiptConstants.THIRD_PARTY_DISCLOSURE, "Yes");
        } else {
            jsonPurposeObj.put(ConsentReceiptConstants.THIRD_PARTY_DISCLOSURE, "No");
        }
        jsonPurposeObj.put(ConsentReceiptConstants.THIRD_PARTY_NAME, purpose
                .getThirdPartyName());

        return jsonPurposeObj;
    }

    private JSONObject setSensitivePersonalInfoCategory(JSONObject jsonObject, List<PiiCategoryDO> piiCategoryList) {

        if (piiCategoryList.isEmpty()) {
            jsonObject.put(ConsentReceiptConstants.SENSITIVE, false);
        } else {
            jsonObject.put(ConsentReceiptConstants.SENSITIVE, true);
            JSONArray sensitivePICatArr = new JSONArray();
            for (PiiCategoryDO piiCategory : piiCategoryList) {
                sensitivePICatArr.add(piiCategory.getPiiCatId() + " - " + piiCategory.getPiiCat());
            }
            jsonObject.put(ConsentReceiptConstants.SENSITIVE_PERSONAL_INFO_CATEGORY, sensitivePICatArr);
        }
        return jsonObject;
    }

    /**
     * Read the consent file string and map it to the object creator.
     *
     * @param consentString string that contains the details about the consents of an user.
     */
    public void readConsentFile(String consentString) throws DataAccessException {

        Gson gson = new Gson();

        JsonObjectCreator objectCreator = gson.fromJson(consentString, JsonObjectCreator.class);
        readConsentReceipt(objectCreator);
    }

    private void readConsentReceipt(JsonObjectCreator objectCreator) throws DataAccessException {

        ConsentDaoImpl consentDaoImpl = new ConsentDaoImpl();

        String uniqueId = UniqueIdUtil.createUniqueId();
        String currentTime = DateTimeUtil.getCurrentDateTime();

        int dataControllerId = consentDaoImpl.isDataControllerExists(objectCreator.getDataController().getOrg());

        /*getting data to actual objects*/
        ConsentDO consentDO = new ConsentDO();
        consentDO.setCollectionMethod(objectCreator.getCollectionMethod());
        consentDO.setSGUID(uniqueId);
        consentDO.setPiiPrincipalId(objectCreator.getSubject());
        consentDO.setConsentTimestamp(currentTime);

        DataControllerDO dataController = new DataControllerDO();
        if (dataControllerId != 0) {
            dataController.setDataControllerId(dataControllerId);
        } else {
            dataController.setOrgName(objectCreator.getDataController().getOrg());
            dataController.setContactName(objectCreator.getDataController().getContact());
            dataController.setStreet(objectCreator.getDataController().getAddress().getStreetAddress());
            dataController.setCountry(objectCreator.getDataController().getAddress().getAddressCountry());
            dataController.setEmail(objectCreator.getDataController().getEmail());
            dataController.setPhoneNo(objectCreator.getDataController().getPhone());
            dataController.setPublicKey(objectCreator.getPublicKey());
            dataController.setPolicyUrl(objectCreator.getPolicyUrl());
            consentDaoImpl.addDataController(dataController);
            dataControllerId = consentDaoImpl.isDataControllerExists(objectCreator.getDataController().getOrg());
            dataController.setDataControllerId(dataControllerId);
        }
        consentDO.setDataController(dataController);

        ServicesDO[] services = new ServicesDO[objectCreator.getServices().length];
        for (int i = 0; i < objectCreator.getServices().length; i++) {
            services[i] = new ServicesDO();
            int serviceId = 0;
            try {
                serviceId = consentDaoImpl.getServiceIdByService(objectCreator.getServices()[i].getServiceName());
            } catch (DataAccessException e) {
                throw new DataAccessException("Database error. Could not get the serviceId.", e);
            }
            PurposeDetailsDO[] purposeDO = new PurposeDetailsDO[objectCreator.getServices()[i].getPurposes()
                    .length];
            if (serviceId != 0) {
                services[i].setServiceId(serviceId);
                for (int j = 0; j < objectCreator.getServices()[i].getPurposes().length; j++) {
                    purposeDO[j] = new PurposeDetailsDO();
                    int purposeId = 0;
                    try {
                        purposeId = consentDaoImpl.getPurposeIdByPurpose(objectCreator.getServices()[i].getPurposes()[j]
                                .getPurpose());
                    } catch (DataAccessException e) {
                        throw new DataAccessException("Database error. Could not get the purposeId.", e);
                    }
                    if (purposeId != 0) {
                        purposeDO[j].setPurposeId(purposeId);
                        purposeDO[j].setTimestamp(currentTime);
                        purposeDO[j].setCollectionMethod("Web-Form");
                    }
                }
            }
            services[i].setPurposeDetails(purposeDO);
        }
        consentDaoImpl.addUserConsentDetails(consentDO, services);
    }
}
