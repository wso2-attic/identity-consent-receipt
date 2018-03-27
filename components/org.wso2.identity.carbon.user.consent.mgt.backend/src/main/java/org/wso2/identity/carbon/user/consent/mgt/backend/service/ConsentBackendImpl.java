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

package org.wso2.identity.carbon.user.consent.mgt.backend.service;

import org.json.simple.JSONObject;
import org.wso2.identity.carbon.user.consent.mgt.backend.dao.impl.ConsentDaoImpl;
import org.wso2.identity.carbon.user.consent.mgt.backend.exception.DataAccessException;
import org.wso2.identity.carbon.user.consent.mgt.backend.jsonparser.JsonParser;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.*;

import java.util.List;

/**
 * Class that implements the ConsentBackend interface methods to access the DAO impl.
 */
public class ConsentBackendImpl implements ConsentBackend {

    private ConsentDaoImpl consentDaoImpl = new ConsentDaoImpl();

    @Override
    public JSONObject getCreatedConsentReceipt(String subjectName) throws DataAccessException {

        JsonParser jsonParser = new JsonParser();
        return jsonParser.createConsentReceipt(subjectName);
    }

    @Override
    public void readConsentReceipt(String consentString) throws DataAccessException {

        JsonParser jsonParser = new JsonParser();
        jsonParser.readConsentFile(consentString);
    }

    @Override
    public List<ServicesDO> getServicesForUserView(String subjectName) throws DataAccessException {

        return consentDaoImpl.getServicesForUserView(subjectName);
    }

    @Override
    public ServicesDO getServiceByUserByServiceId(String subjectName, int serviceId) throws DataAccessException {

        return consentDaoImpl.getServiceByUserByServiceId(subjectName, serviceId);
    }

    @Override
    public PurposeDetailsDO getPurposeByUserByServiceByPurposeId(String subjectName, int serviceId, int purposeId)
            throws DataAccessException {

        return consentDaoImpl.getPurposeByUserByService(subjectName, serviceId, purposeId);
    }

    @Override
    public List<ServicesDO> getServicesByUserByThirdParty(String subjectName, int thirdPartyId)
            throws DataAccessException {

        return consentDaoImpl.getServiceDetailsByThirdParty(subjectName, thirdPartyId);
    }

    //-- Data Controller Configurations
    @Override
    public DataControllerDO setDataController(DataControllerDO dataControllerDO) throws DataAccessException {

        return consentDaoImpl.addDataController(dataControllerDO);
    }

    @Override
    public List<DataControllerDO> getDataControllerList() throws DataAccessException {

        return consentDaoImpl.getDataControllerList();
    }

    @Override
    public DataControllerDO getDataControllerById(int id) throws DataAccessException {

        return consentDaoImpl.getDataController(id);
    }

    @Override
    public DataControllerDO updateDataController(DataControllerDO dataControllerDO) throws DataAccessException {

        return consentDaoImpl.updateDataController(dataControllerDO);
    }

    @Override
    public DataControllerDO deleteDataController(int dataControllerId) throws DataAccessException {

        return consentDaoImpl.deleteDataController(dataControllerId);
    }

    //-- Personally Identifiable Info Category Configuration
    @Override
    public PiiCategoryDO setPersonalInfoCat(PiiCategoryDO piiCategoryDO) throws DataAccessException {

        return consentDaoImpl.addPiiCategory(piiCategoryDO);
    }

    @Override
    public List<PiiCategoryDO> getPersonalIdentifyInfoCat() throws DataAccessException {

        return consentDaoImpl.getPersonalInfoCatForConfig();
    }

    @Override
    public PiiCategoryDO updatePersonalInfoCat(PiiCategoryDO piiCategoryDO) throws DataAccessException {

        return consentDaoImpl.updatePersonalInfoCat(piiCategoryDO);
    }

    @Override
    public PiiCategoryDO deletePersonalInfoCat(int categoryId) throws DataAccessException {

        return consentDaoImpl.deletePersonalInfoCat(categoryId);
    }

    @Override
    public PiiCategoryDO getPersonalInfoCatById(int categoryId) throws DataAccessException {

        return consentDaoImpl.getPersonalInfoCatById(categoryId);
    }

    //- Purpose Configuration
    @Override
    public PurposeDetailsDO setPurpose(PurposeDetailsDO purpose) throws DataAccessException {

        return consentDaoImpl.addPurposeDetails(purpose);
    }

    @Override
    public List<PurposeDetailsDO> getPurposeDetailsForConf() throws DataAccessException {

        return consentDaoImpl.getPurposesForConfig();
    }

    @Override
    public PurposeDetailsDO updatePurpose(PurposeDetailsDO purpose) throws DataAccessException {

        return consentDaoImpl.updatePurposeDetails(purpose);
    }

    @Override
    public PurposeDetailsDO deletePurpose(int purposeId) throws DataAccessException {

        return consentDaoImpl.deletePurpose(purposeId);
    }

    @Override
    public PurposeDetailsDO getPurposeDetailsById(int id) throws DataAccessException {

        return consentDaoImpl.getPurposeDetailsById(id);
    }

    //-- Service Configuration
    @Override
    public ServicesDO setService(ServicesDO service) throws DataAccessException {

        return consentDaoImpl.addServiceDetails(service);
    }

    @Override
    public List<ServicesDO> getServicesForConf() throws DataAccessException {

        return consentDaoImpl.getServicesForConf();
    }

    @Override
    public ServicesDO updateService(ServicesDO service) throws DataAccessException {

        return consentDaoImpl.updateServiceDetails(service);
    }

    @Override
    public ServicesDO deleteService(int serviceId) throws DataAccessException {

        return consentDaoImpl.deleteService(serviceId);
    }

    @Override
    public ServicesDO getServiceById(int id) throws DataAccessException {

        return consentDaoImpl.getServiceById(id);
    }

    //-- Purpose Category Configuration
    @Override
    public List<PurposeCategoryDO> getPurposeCategories() throws DataAccessException {

        return consentDaoImpl.getPurposeCategories();
    }

    @Override
    public PurposeCategoryDO setPurposeCategory(PurposeCategoryDO purposeCategory) throws DataAccessException {

        return consentDaoImpl.addPurposeCategory(purposeCategory);
    }

    @Override
    public PurposeCategoryDO updatePurposeCategory(PurposeCategoryDO purposeCategory) throws DataAccessException {

        return consentDaoImpl.updatePurposeCategory(purposeCategory);
    }

    @Override
    public PurposeCategoryDO deletePurposeCategory(int categoryId) throws DataAccessException {

        return consentDaoImpl.deletePurposeCategory(categoryId);
    }

    @Override
    public PurposeCategoryDO getPurposeCategoryById(int id) throws DataAccessException {

        return consentDaoImpl.getPurposeCategoryById(id);
    }

    //-- Third Party Configuration
    @Override
    public List<ThirdPartyDO> getThirdParties() throws DataAccessException {

        return consentDaoImpl.getThirdPartyDetailsForConf();
    }

    @Override
    public ThirdPartyDO setThirdParty(ThirdPartyDO thirdParty) throws DataAccessException {

        return consentDaoImpl.addThirdParty(thirdParty);
    }

    @Override
    public ThirdPartyDO updateThirdParty(ThirdPartyDO thirdParty) throws DataAccessException {

        return consentDaoImpl.updateThirdParty(thirdParty);
    }

    @Override
    public ThirdPartyDO getThirdPartyById(int id) throws DataAccessException {

        return consentDaoImpl.getThirdPartyById(id);
    }

    @Override
    public ConsentDO getSubjectName(String subjectName) throws DataAccessException {

        return consentDaoImpl.getUserNameFromSGUID(subjectName);
    }

    @Override
    public void setConsentDetailsForUser(ConsentDO consentDO, ServicesDO[] services) throws DataAccessException {

        consentDaoImpl.addUserConsentDetails(consentDO, services);
    }

    @Override
    public void revokeConsent(String subjectName, List<ServicesDO> servicesList) throws DataAccessException {

        consentDaoImpl.revokeConsentByUser(subjectName, servicesList);
    }
}
