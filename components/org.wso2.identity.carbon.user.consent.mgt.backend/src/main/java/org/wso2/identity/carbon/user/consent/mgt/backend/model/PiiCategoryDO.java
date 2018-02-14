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
 * Class that keeps the personally identifiable info category details.
 */
public class PiiCategoryDO {

    private int purposeId;
    private int piiCatId;
    private String piiCat;
    private String piiCatDescription;
    private int sensitivity;

    public PiiCategoryDO() {

    }

    public PiiCategoryDO(int piiCatId, String piiCat, int sensitivity) {

        this.piiCatId = piiCatId;
        this.piiCat = piiCat;
        this.sensitivity = sensitivity;
    }

    public PiiCategoryDO(int piiCatId, String piiCat, String piiCatDescription, int sensitivity) {

        this.piiCatId = piiCatId;
        this.piiCat = piiCat;
        this.piiCatDescription = piiCatDescription;
        this.sensitivity = sensitivity;
    }

    public int getPurposeId() {

        return purposeId;
    }

    public void setPurposeId(int purposeId) {

        this.purposeId = purposeId;
    }

    public int getPiiCatId() {

        return piiCatId;
    }

    public void setPiiCatId(int piiCatId) {

        this.piiCatId = piiCatId;
    }

    public String getPiiCat() {

        return piiCat;
    }

    public void setPiiCat(String piiCat) {

        this.piiCat = piiCat;
    }

    public int getSensitivity() {

        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {

        this.sensitivity = sensitivity;
    }

    public String getPiiCatDescription() {

        return piiCatDescription;
    }

    public void setPiiCatDescription(String piiCatDescription) {

        this.piiCatDescription = piiCatDescription;
    }
}
