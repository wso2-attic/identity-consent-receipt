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

/**
 * Class that contains the fields to keep the consent receipt JSON fileds.
 */
public class JsonObjectCreator {

    private String subject;
    private String collectionMethod;
    private String jurisdiction;
    private String version;
    private String consentTimestamp;
    private JsonDataController dataController;
    private String publicKey;
    private String policyUrl;
    private JsonService[] services;

    public JsonObjectCreator(String subject, String collectionMethod, String jurisdiction, String version, String
            consentTimestamp, JsonService[] services) {

        this.subject = subject;
        this.collectionMethod = collectionMethod;
        this.jurisdiction = jurisdiction;
        this.version = version;
        this.consentTimestamp = consentTimestamp;
        this.services = services;
    }

    public JsonObjectCreator() {

    }

    public String getSubject() {

        return subject;
    }

    public void setSubject(String subject) {

        this.subject = subject;
    }

    public String getCollectionMethod() {

        return collectionMethod;
    }

    public void setCollectionMethod(String collectionMethod) {

        this.collectionMethod = collectionMethod;
    }

    public String getJurisdiction() {

        return jurisdiction;
    }

    public void setJurisdiction(String jurisdiction) {

        this.jurisdiction = jurisdiction;
    }

    public String getVersion() {

        return version;
    }

    public void setVersion(String version) {

        this.version = version;
    }

    public String getConsentTimestamp() {

        return consentTimestamp;
    }

    public void setConsentTimestamp(String consentTimestamp) {

        this.consentTimestamp = consentTimestamp;
    }

    public JsonService[] getServices() {

        return services;
    }

    public void setServices(JsonService[] services) {

        this.services = services;
    }

    public JsonDataController getDataController() {

        return dataController;
    }

    public void setDataController(JsonDataController dataControllerId) {

        this.dataController = dataController;
    }

    public String getPolicyUrl() {

        return policyUrl;
    }

    public void setPolicyUrl(String policyUrl) {

        this.policyUrl = policyUrl;
    }

    public String getPublicKey() {

        return publicKey;
    }

    public void setPublicKey(String publicKey) {

        this.publicKey = publicKey;
    }
}
