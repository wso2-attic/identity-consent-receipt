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
 * Class that keeps the details of the data controller.
 */
public class DataControllerDO {

    private int dataControllerId;
    private String orgName;
    private String contactName;
    private String street;
    private String country;
    private String email;
    private String phoneNo;
    private String publicKey;
    private String policyUrl;

    public DataControllerDO() {

    }

    public DataControllerDO(int dataControllerId, String organizationName, String contactName, String street, String country, String email, String phoneNo, String publicKey, String policyUrl) {

        this.dataControllerId = dataControllerId;
        this.orgName = organizationName;
        this.contactName = contactName;
        this.street = street;
        this.country = country;
        this.email = email;
        this.phoneNo = phoneNo;
        this.publicKey = publicKey;
        this.policyUrl = policyUrl;
    }

    public int getDataControllerId() {

        return this.dataControllerId;
    }

    public void setDataControllerId(int dataControllerId) {

        this.dataControllerId = dataControllerId;
    }

    public String getOrgName() {

        return this.orgName;
    }

    public void setOrgName(String orgName) {

        this.orgName = orgName;
    }

    public String getContactName() {

        return this.contactName;
    }

    public void setContactName(String contactName) {

        this.contactName = contactName;
    }

    public String getStreet() {

        return this.street;
    }

    public void setStreet(String street) {

        this.street = street;
    }

    public String getCountry() {

        return this.country;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public String getEmail() {

        return this.email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getPhoneNo() {

        return this.phoneNo;
    }

    public void setPhoneNo(String phoneNo) {

        this.phoneNo = phoneNo;
    }

    public String getPublicKey() {

        return this.publicKey;
    }

    public void setPublicKey(String publicKey) {

        this.publicKey = publicKey;
    }

    public String getPolicyUrl() {

        return this.policyUrl;
    }

    public void setPolicyUrl(String policyUrl) {

        this.policyUrl = policyUrl;
    }
}
