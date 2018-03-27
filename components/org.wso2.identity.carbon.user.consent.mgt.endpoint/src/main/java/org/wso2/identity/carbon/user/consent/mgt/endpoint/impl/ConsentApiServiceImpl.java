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

package org.wso2.identity.carbon.user.consent.mgt.endpoint.impl;

import org.json.simple.JSONObject;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.identity.carbon.user.consent.mgt.backend.exception.DataAccessException;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.ConsentDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.DataControllerDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.PiiCategoryDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.PurposeCategoryDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.PurposeDetailsDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.ServicesDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.ThirdPartyDO;
import org.wso2.identity.carbon.user.consent.mgt.backend.service.ConsentBackend;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.ApiResponseMessage;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.ConsentApiService;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.ConsentByThirdPartyDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.ConsentReceiptDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.ConsentRevokeListDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.DataControllerInputDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.ErrorDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.PiiCategoryDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.PurposeCategoryDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.PurposeDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.ServiceCRDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.ServiceListDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.ServiceWebFormDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.ThirdPartyDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.dto.UserConsentWebFormDTO;
import org.wso2.identity.carbon.user.consent.mgt.endpoint.mapping.ConsentMapping;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * Class that access the methods of the consent backend to operate on the data in the database.
 */
public class ConsentApiServiceImpl extends ConsentApiService {

    private static ConsentBackend getConsentService() {

        return (ConsentBackend) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(ConsentBackend.class, null);
    }

    @Override
    public Response consentConfigurationDataControllerPost(DataControllerInputDTO dataController) {

        DataControllerDO dataControllerDO = ConsentMapping.setConsentConfigurationDataController(dataController);
        DataControllerInputDTO dataControllerDTO;
        try {
            dataControllerDO = getConsentService().setDataController(dataControllerDO);
            dataControllerDTO = ConsentMapping.getConsentConfigurationDataController(dataControllerDO);
            return Response.ok().entity(dataControllerDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }

    }

    @Override
    public Response consentConfigurationDataControllerGet() {

        List<DataControllerInputDTO> dataControllerInputDTOList;
        try {
            List<DataControllerDO> dataControllerList = getConsentService().getDataControllerList();
            dataControllerInputDTOList = ConsentMapping.getConsentConfigurationDataController(dataControllerList);
            return Response.ok().entity(dataControllerInputDTOList).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationDataControllerPut(DataControllerInputDTO dataController) {

        DataControllerDO dataControllerDO = ConsentMapping.updateDataController(dataController);
        DataControllerInputDTO dataControllerOutDTO;
        try {
            dataControllerDO = getConsentService().updateDataController(dataControllerDO);
            dataControllerOutDTO = ConsentMapping.getConsentConfigurationDataController(dataControllerDO);
            return Response.ok().entity(dataControllerOutDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationDataControllerDelete(Integer dataControllerId) {

        DataControllerInputDTO deletedDataControllerDTO = new DataControllerInputDTO();
        try {
            DataControllerDO dataControllerDO = getConsentService().deleteDataController(dataControllerId);
            deletedDataControllerDTO.setOrg(dataControllerDO.getOrgName());
            return Response.ok().entity(deletedDataControllerDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationDataControllerIdGet(Integer dataControllerId) {

        DataControllerInputDTO dataControllerOutputDTO;
        try {
            DataControllerDO dataController = getConsentService().getDataControllerById(dataControllerId);
            dataControllerOutputDTO = ConsentMapping.getConsentConfigurationDataController(dataController);
            return Response.ok().entity(dataControllerOutputDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPersonalInfoCategoryGet() {

        List<PiiCategoryDTO> piiCategoryDTOList;
        try {
            List<PiiCategoryDO> piiCategoryDOList = getConsentService().getPersonalIdentifyInfoCat();
            piiCategoryDTOList = ConsentMapping.getConsentConfigurationPersonalInfoCategory(piiCategoryDOList);
            return Response.ok().entity(piiCategoryDTOList).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPersonalInfoCategoryPost(PiiCategoryDTO piiCategory) {

        PiiCategoryDO piiCategoryDO = ConsentMapping.setConsentConfigurationPersonalInfoCategory(piiCategory);
        PiiCategoryDTO piiCategoryDTO;
        try {
            PiiCategoryDO piiCategoryOut = getConsentService().setPersonalInfoCat(piiCategoryDO);
            piiCategoryDTO = ConsentMapping.setPiiCategoryDOToPiiCategoryDTO(piiCategoryOut);
            return Response.ok().entity(piiCategoryDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPersonalInfoCategoryPut(PiiCategoryDTO piiCategory) {

        PiiCategoryDO piiCategoryDO = ConsentMapping.updatePersonallyIdentifiableInfoCat(piiCategory);
        PiiCategoryDTO piiCategoryDTO;
        try {
            PiiCategoryDO piiCategoryDOUpdated = getConsentService().updatePersonalInfoCat(piiCategoryDO);
            piiCategoryDTO = ConsentMapping.setPiiCategoryDOToPiiCategoryDTO(piiCategoryDOUpdated);
            return Response.ok().entity(piiCategoryDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPersonalInfoCategoryDelete(Integer categoryId) {

        PiiCategoryDTO piiCategoryDTO = new PiiCategoryDTO();
        try {
            PiiCategoryDO piiCategory = getConsentService().deletePersonalInfoCat(categoryId);
            piiCategoryDTO.setPiiCat(piiCategory.getPiiCat());
            return Response.ok().entity(piiCategoryDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPersonalInfoCatIdGet(Integer categoryId) {

        PiiCategoryDTO piiCategoryDTO;

        try {
            PiiCategoryDO piiCategory = getConsentService().getPersonalInfoCatById(categoryId);
            piiCategoryDTO = ConsentMapping.setPiiCategoryDOToPiiCategoryDTO(piiCategory);
            return Response.ok().entity(piiCategoryDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposeGet() {

        List<PurposeDTO> purposeDTOList;
        try {
            List<PurposeDetailsDO> purposeDetailsDOList = getConsentService().getPurposeDetailsForConf();
            purposeDTOList = ConsentMapping.getConsentConfigurationPurpose(purposeDetailsDOList);
            return Response.ok().entity(purposeDTOList).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposePost(PurposeDTO purpose) {

        PurposeDetailsDO purposeDetailsDO = ConsentMapping.setPurposeDTOToPurposeDetailsDO(purpose);
        PurposeDTO purposeDTO;
        try {
            PurposeDetailsDO purposeDetailsDONew = getConsentService().setPurpose(purposeDetailsDO);
            purposeDTO = ConsentMapping.setPurposeDetailsDOToPurposeDTO(purposeDetailsDONew);
            return Response.ok().entity(purposeDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposePut(PurposeDTO purpose) {

        PurposeDetailsDO purposeDO = ConsentMapping.setPurposeDTOToPurposeDetailsDO(purpose);
        purposeDO.setPurposeId(purpose.getPurposeId());
        PurposeDTO updatedPurposeDTO;
        try {
            PurposeDetailsDO updatedPurposeDO = getConsentService().updatePurpose(purposeDO);
            updatedPurposeDTO = ConsentMapping.setPurposeDetailsDOToPurposeDTO(updatedPurposeDO);
            return Response.ok().entity(updatedPurposeDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposeDelete(Integer id) {

        PurposeDTO deletedPurposeDTO = new PurposeDTO();
        try {
            PurposeDetailsDO deletedPurposeDO = getConsentService().deletePurpose(id);
            deletedPurposeDTO.setPurpose(deletedPurposeDO.getPurpose());
            return Response.ok().entity(deletedPurposeDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposeIdGet(Integer categoryId) {

        PurposeDTO purposeDTO;
        try {
            PurposeDetailsDO purpose = getConsentService().getPurposeDetailsById(categoryId);
            purposeDTO = ConsentMapping.setPurposeDetailsDOToPurposeDTO(purpose);
            return Response.ok().entity(purposeDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationServiceGet() {

        List<ServiceWebFormDTO> serviceDTOList;
        try {
            List<ServicesDO> servicesDOList = getConsentService().getServicesForConf();
            serviceDTOList = ConsentMapping.setServicesDOListToServiceWebFormDTOList(servicesDOList);
            return Response.ok().entity(serviceDTOList).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationServicePost(ServiceWebFormDTO service) {

        ServicesDO servicesDO = new ServicesDO();
        ServiceWebFormDTO addedServiceDTO;

        servicesDO.setServiceDescription(service.getServiceName());
        PurposeDetailsDO[] purposeDOArr = new PurposeDetailsDO[service.getPurposes().size()];
        for (int i = 0; i < service.getPurposes().size(); i++) {
            purposeDOArr[i] = new PurposeDetailsDO();
            purposeDOArr[i].setPurposeId(service.getPurposes().get(i).getPurposeId());
        }
        servicesDO.setPurposeDetails(purposeDOArr);
        try {
            ServicesDO addedServiceDO = getConsentService().setService(servicesDO);
            addedServiceDTO = ConsentMapping.setServiceDOToServiceWebFormDTO(addedServiceDO);
            return Response.ok().entity(addedServiceDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationServicePut(ServiceWebFormDTO service) {

        ServicesDO servicesDO = new ServicesDO();
        ServiceWebFormDTO updatedServiceDTO;

        servicesDO.setServiceId(service.getServiceId());
        servicesDO.setServiceDescription(service.getServiceName());
        PurposeDetailsDO[] purposeDOS = new PurposeDetailsDO[service.getPurposes().size()];
        for (int i = 0; i < service.getPurposes().size(); i++) {
            purposeDOS[i] = new PurposeDetailsDO();
            purposeDOS[i].setPurposeId(service.getPurposes().get(i).getPurposeId());
        }
        servicesDO.setPurposeDetails(purposeDOS);
        try {
            ServicesDO updatedServiceDO = getConsentService().updateService(servicesDO);
            updatedServiceDTO = ConsentMapping.setServiceDOToServiceWebFormDTO(updatedServiceDO);
            return Response.ok().entity(updatedServiceDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationServiceDelete(Integer id) {

        ServiceWebFormDTO serviceDTO = new ServiceWebFormDTO();
        try {
            ServicesDO service = getConsentService().deleteService(id);
            serviceDTO.setServiceName(service.getServiceDescription());
            return Response.ok().entity(serviceDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationServiceIdGet(Integer categoryId) {

        ServiceWebFormDTO serviceDTO;
        try {
            ServicesDO servicesDO = getConsentService().getServiceById(categoryId);
            serviceDTO = ConsentMapping.setServiceDOToServiceWebFormDTO(servicesDO);
            return Response.ok().entity(serviceDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentReceiptPost(ConsentReceiptDTO userDetails) {

        String jsonString = userDetails.toString();
        try {
            getConsentService().readConsentReceipt(jsonString);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "Consents were added."))
                    .build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentReceiptWebFormPost(UserConsentWebFormDTO userConsentWebForm) {

        ConsentDO consentDO = ConsentMapping.setUserAndDataController(userConsentWebForm);
        ServicesDO[] servicesDOS = ConsentMapping.setUserConsents(userConsentWebForm);
        try {
            getConsentService().setConsentDetailsForUser(consentDO, servicesDOS);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "Consents were added."))
                    .build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentRevokePut(ConsentRevokeListDTO revokingConsent) {

        List<ServicesDO> servicesList = ConsentMapping.revokeConsent(revokingConsent);
        try {
            getConsentService().revokeConsent(revokingConsent.getSubjectName(), servicesList);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "Revoke completed."))
                    .build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentUserNameReceiptGet(String subjectName) {

        JSONObject jsonObject;
        try {
            jsonObject = getConsentService().getCreatedConsentReceipt(subjectName);
            return Response.ok().entity(jsonObject).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentUserNameServiceListGet(String subjectName) {

        ServiceListDTO serviceListDTO;
        try {
            List<ServicesDO> servicesDOList = getConsentService().getServicesForUserView(subjectName);
            serviceListDTO = ConsentMapping.getConsentSubjectNameServiceList(servicesDOList);
            return Response.ok().entity(serviceListDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentUserNameServicesServiceIdGet(String subjectName, Integer serviceId) {

        ServiceCRDTO serviceCRDTO;
        try {
            ServicesDO servicesDO = getConsentService().getServiceByUserByServiceId(subjectName, serviceId);
            serviceCRDTO = ConsentMapping.getConsentSubjectNameServicesServiceId(servicesDO);
            return Response.ok().entity(serviceCRDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentUserNameServicesServiceIdPurposeGet(String subjectName, Integer serviceId, Integer
            purposeId) {

        PurposeDTO purposeDTO;
        try {
            PurposeDetailsDO purposeDetailsDO = getConsentService().getPurposeByUserByServiceByPurposeId(subjectName,
                    serviceId, purposeId);
            purposeDTO = ConsentMapping.getConsentSubjectNameServicesServiceIdPurpose(purposeDetailsDO);
            return Response.ok().entity(purposeDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentUserNameThirdPartyGet(String subjectName, Integer thirdPartyId) {

        ConsentByThirdPartyDTO consent;
        try {
            List<ServicesDO> servicesDOList = getConsentService().getServicesByUserByThirdParty(subjectName, thirdPartyId);
            consent = ConsentMapping.getConsentSubjectNameThirdParty(servicesDOList);
            return Response.ok().entity(consent).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposeCategoryGet() {

        List<PurposeCategoryDTO> purposeCategoryDTOList;
        try {
            List<PurposeCategoryDO> purposeCategoryList = getConsentService().getPurposeCategories();
            purposeCategoryDTOList = ConsentMapping.getPurposeCategories(purposeCategoryList);
            return Response.ok().entity(purposeCategoryDTOList).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposeCategoryPost(PurposeCategoryDTO purposeCategory) {

        PurposeCategoryDTO addedPurposeCatDTO;
        PurposeCategoryDO purposeCategoryDO = ConsentMapping.setPurposeCategory(purposeCategory);
        try {
            PurposeCategoryDO addedPurposeCatDO = getConsentService().setPurposeCategory(purposeCategoryDO);
            addedPurposeCatDTO = ConsentMapping.setPurposeCategoryDOToPurposeCategoryDTO(addedPurposeCatDO);
            return Response.ok().entity(addedPurposeCatDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposeCategoryPut(PurposeCategoryDTO purposeCategory) {

        PurposeCategoryDTO updatedPurposeCatDTO;
        PurposeCategoryDO purposeCategoryDO = ConsentMapping.updatePurposeCategory(purposeCategory);
        try {
            PurposeCategoryDO updatedPurposeCatDO = getConsentService().updatePurposeCategory(purposeCategoryDO);
            updatedPurposeCatDTO = ConsentMapping.setPurposeCategoryDOToPurposeCategoryDTO(updatedPurposeCatDO);
            return Response.ok().entity(updatedPurposeCatDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposeCategoryDelete(Integer purposeCategoryId) {

        PurposeCategoryDTO purposeCategoryDTO = new PurposeCategoryDTO();
        try {
            PurposeCategoryDO purposeCategory = getConsentService().deletePurposeCategory(purposeCategoryId);
            purposeCategoryDTO.setPurposeCategoryShortCode(purposeCategory.getPurposeCatShortCode());
            return Response.ok().entity(purposeCategoryDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationPurposeCategoryIdGet(Integer categoryId) {

        PurposeCategoryDTO purposeCategoryDTO;
        try {
            PurposeCategoryDO purposeCategoryDO = getConsentService().getPurposeCategoryById(categoryId);
            purposeCategoryDTO = ConsentMapping.setPurposeCategoryDOToPurposeCategoryDTO(purposeCategoryDO);
            return Response.ok().entity(purposeCategoryDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationThirdPartyGet() {

        List<ThirdPartyDTO> thirdPartyDTOList = new ArrayList<>();
        try {
            List<ThirdPartyDO> thirdPartyList = getConsentService().getThirdParties();
            for (ThirdPartyDO thirdParty : thirdPartyList) {
                ThirdPartyDTO thirdPartyDTO = new ThirdPartyDTO();
                thirdPartyDTO.setThirdPartyId(thirdParty.getThirdPartyId());
                thirdPartyDTO.setThirdPartyName(thirdParty.getThirdPartyName());
                thirdPartyDTOList.add(thirdPartyDTO);
            }
            return Response.ok().entity(thirdPartyDTOList).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationThirdPartyPost(ThirdPartyDTO thirdParty) {

        ThirdPartyDO thirdPartyDO = new ThirdPartyDO();
        ThirdPartyDTO addedThirdPartyDTO = new ThirdPartyDTO();
        thirdPartyDO.setThirdPartyName(thirdParty.getThirdPartyName());
        try {
            ThirdPartyDO addedThirdPartyDO = getConsentService().setThirdParty(thirdPartyDO);
            addedThirdPartyDTO.setThirdPartyId(addedThirdPartyDO.getThirdPartyId());
            addedThirdPartyDTO.setThirdPartyName(addedThirdPartyDO.getThirdPartyName());
            return Response.ok().entity(addedThirdPartyDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationThirdPartyPut(ThirdPartyDTO thirdParty) {

        ThirdPartyDO thirdPartyDO = new ThirdPartyDO();
        ThirdPartyDTO updatedThirdPartyDTO = new ThirdPartyDTO();
        thirdPartyDO.setThirdPartyId(thirdParty.getThirdPartyId());
        thirdPartyDO.setThirdPartyName(thirdParty.getThirdPartyName());
        try {
            ThirdPartyDO updatedThirdPartyDO = getConsentService().updateThirdParty(thirdPartyDO);
            updatedThirdPartyDTO.setThirdPartyId(updatedThirdPartyDO.getThirdPartyId());
            updatedThirdPartyDTO.setThirdPartyName(updatedThirdPartyDO.getThirdPartyName());
            return Response.ok().entity(updatedThirdPartyDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    @Override
    public Response consentConfigurationThirdPartyIdGet(Integer categoryId) {

        ThirdPartyDTO thirdPartyDTO = new ThirdPartyDTO();
        thirdPartyDTO.setThirdPartyId(1);
        try {
            ThirdPartyDO thirdPartyDO = getConsentService().getThirdPartyById(categoryId);
            thirdPartyDTO.setThirdPartyId(thirdPartyDO.getThirdPartyId());
            thirdPartyDTO.setThirdPartyName(thirdPartyDO.getThirdPartyName());
            return Response.ok().entity(thirdPartyDTO).build();
        } catch (DataAccessException e) {
            return Response.serverError().entity(handleException(e)).build();
        }
    }

    private ErrorDTO handleException(DataAccessException e) {

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setErrorCode(e.getErrorCode());
        errorDTO.setErrorDescription(e.getMessage());
        errorDTO.setErrorCause(e.getCause().getMessage());
        return errorDTO;
    }
}
