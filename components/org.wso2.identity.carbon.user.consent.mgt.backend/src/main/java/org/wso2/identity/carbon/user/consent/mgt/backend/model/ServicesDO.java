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

package org.wso2.identity.carbon.user.consent.mgt.backend.model;

/**
 * Class that keeps the details of the services.
 */
public class ServicesDO {

    private int serviceId;
    private String serviceDescription;
    private PurposeDetailsDO purposeDetails;
    private PurposeDetailsDO[] purposeDetailsArr;

    public ServicesDO() {

    }

    public ServicesDO(int serviceId, String serviceDescription, PurposeDetailsDO[] purposeDetails) {

        this.serviceId = serviceId;
        this.serviceDescription = serviceDescription;
        this.purposeDetailsArr = purposeDetails;
    }

    public ServicesDO(int serviceId, String serviceDescription, PurposeDetailsDO purposeDetails) {

        this.serviceId = serviceId;
        this.serviceDescription = serviceDescription;
        this.purposeDetails = purposeDetails;
    }

    public int getServiceId() {

        return serviceId;
    }

    public void setServiceId(int serviceId) {

        this.serviceId = serviceId;
    }

    public String getServiceDescription() {

        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {

        this.serviceDescription = serviceDescription;
    }

    public void setPurposeDetails(PurposeDetailsDO purposeDetails) {

        this.purposeDetails = purposeDetails;
    }

    public PurposeDetailsDO getPurposeDetails() {

        return this.purposeDetails;
    }

    public void setPurposeDetails(PurposeDetailsDO[] purposeDetails) {

        this.purposeDetailsArr = purposeDetails;

    }

    public PurposeDetailsDO[] getPurposeDetailsArr() {

        return purposeDetailsArr;
    }
}
