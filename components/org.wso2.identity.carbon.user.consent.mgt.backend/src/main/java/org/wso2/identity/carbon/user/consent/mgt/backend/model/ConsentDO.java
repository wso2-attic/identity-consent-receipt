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
 * Class that keep the details of the user.
 */
public class ConsentDO {

    private String version;
    private String jurisdiction;
    private String collectionMethod;
    private String SGUID;
    private String piiPrincipalId;
    private String consentTimestamp;
    private DataControllerDO dataController;

    public ConsentDO() {

    }

    public ConsentDO(String collectionMethod, String SGUID, String piiPrincipalId, String consentTimestamp, DataControllerDO dataController) {

        this.collectionMethod = collectionMethod;
        this.SGUID = SGUID;
        this.piiPrincipalId = piiPrincipalId;
        this.consentTimestamp = consentTimestamp;
        this.dataController = dataController;
    }

    public DataControllerDO getDataController() {

        return dataController;
    }

    public void setDataController(DataControllerDO dataController) {

        this.dataController = dataController;
    }

    public String getVersion() {

        return version;
    }

    public void setVersion(String version) {

        this.version = version;
    }

    public String getJurisdiction() {

        return jurisdiction;
    }

    public void setJurisdiction(String jurisdiction) {

        this.jurisdiction = jurisdiction;
    }

    public String getCollectionMethod() {

        return collectionMethod;
    }

    public void setCollectionMethod(String collectionMethod) {

        this.collectionMethod = collectionMethod;
    }

    public String getSGUID() {

        return SGUID;
    }

    public void setSGUID(String SGUID) {

        this.SGUID = SGUID;
    }

    public String getPiiPrincipalId() {

        return piiPrincipalId;
    }

    public void setPiiPrincipalId(String subject) {

        this.piiPrincipalId = subject;
    }

    public String getConsentTimestamp() {

        return consentTimestamp;
    }

    public void setConsentTimestamp(String consentTimestamp) {

        this.consentTimestamp = consentTimestamp;
    }

}
