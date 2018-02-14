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

package org.wso2.identity.carbon.user.consent.mgt.backend.jsonParser;

/**
 * Class that contains the fields to keep the JSON address fields.
 */
public class JSONAddress {

    private String addressCountry;
    private String streetAddress;

    public String getAddressCountry() {

        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {

        this.addressCountry = addressCountry;
    }

    public String getStreetAddress() {

        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {

        this.streetAddress = streetAddress;
    }
}
