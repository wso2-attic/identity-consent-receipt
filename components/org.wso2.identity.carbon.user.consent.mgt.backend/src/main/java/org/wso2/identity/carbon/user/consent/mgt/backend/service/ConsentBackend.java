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
import org.wso2.identity.carbon.user.consent.mgt.backend.exception.DataAccessException;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.*;

import java.util.List;

/**
 * Interface that contains the methods to access the DAO impl.
 */
public interface ConsentBackend {

    public JSONObject getCreatedConsentReceipt(String subjectName) throws DataAccessException;

    public List<ServicesDO> getServicesForUserView(String subjectName) throws DataAccessException;

    public ServicesDO getServiceByUserByServiceId(String subjectName, int serviceId) throws DataAccessException;

    public PurposeDetailsDO getPurposeByUserByServiceByPurposeId(String subjectName, int serviceId, int purposeId)
            throws DataAccessException;

    public List<ServicesDO> getServicesByUserByThirdParty(String subjectName, int thirdPartyId) throws
            DataAccessException;

    public ConsentDO getSubjectName(String subjectName) throws DataAccessException;

    public void setConsentDetailsForUser(ConsentDO consentDO, ServicesDO[] services) throws DataAccessException;

    public void revokeConsent(String subjectName, List<ServicesDO> servicesList) throws DataAccessException;

    public DataControllerDO setDataController(DataControllerDO dataControllerDO) throws DataAccessException;

    public List<DataControllerDO> getDataControllerList() throws DataAccessException;

    public DataControllerDO updateDataController(DataControllerDO dataControllerDO) throws DataAccessException;

    public DataControllerDO deleteDataController(int id) throws DataAccessException;

    public DataControllerDO getDataControllerById(int id) throws DataAccessException;

    public PiiCategoryDO setPersonalInfoCat(PiiCategoryDO piiCategoryDO) throws DataAccessException;

    public List<PiiCategoryDO> getPersonalIdentifyInfoCat() throws DataAccessException;

    public PiiCategoryDO updatePersonalInfoCat(PiiCategoryDO piiCategoryDO) throws DataAccessException;

    public PiiCategoryDO deletePersonalInfoCat(int categoryId) throws DataAccessException;

    public PiiCategoryDO getPersonalInfoCatById(int categoryId) throws DataAccessException;

    public PurposeDetailsDO setPurpose(PurposeDetailsDO purpose) throws DataAccessException;

    public List<PurposeDetailsDO> getPurposeDetailsForConf() throws DataAccessException;

    public PurposeDetailsDO updatePurpose(PurposeDetailsDO purpose) throws DataAccessException;

    public PurposeDetailsDO deletePurpose(int purposeId) throws DataAccessException;

    public PurposeDetailsDO getPurposeDetailsById(int id) throws DataAccessException;

    public ServicesDO setService(ServicesDO service) throws DataAccessException;

    public List<ServicesDO> getServicesForConf() throws DataAccessException;

    public ServicesDO updateService(ServicesDO services) throws DataAccessException;

    public ServicesDO deleteService(int serviceId) throws DataAccessException;

    public ServicesDO getServiceById(int id) throws DataAccessException;

    public PurposeCategoryDO setPurposeCategory(PurposeCategoryDO purposeCategory) throws DataAccessException;

    public List<PurposeCategoryDO> getPurposeCategories() throws DataAccessException;

    public PurposeCategoryDO updatePurposeCategory(PurposeCategoryDO purposeCategory) throws DataAccessException;

    public PurposeCategoryDO deletePurposeCategory(int categoryId) throws DataAccessException;

    public PurposeCategoryDO getPurposeCategoryById(int id) throws DataAccessException;

    public ThirdPartyDO setThirdParty(ThirdPartyDO thirdParty) throws DataAccessException;

    public List<ThirdPartyDO> getThirdParties() throws DataAccessException;

    public ThirdPartyDO updateThirdParty(ThirdPartyDO thirdParty) throws DataAccessException;

    public ThirdPartyDO getThirdPartyById(int id) throws DataAccessException;

    public void readConsentReceipt(String consentString) throws DataAccessException;
}
