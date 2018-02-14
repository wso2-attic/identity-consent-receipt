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
 * Class that keeps the details of the purposes.
 */
public class PurposeDetailsDO {

    private String sguid;
    private int purposeId;
    private String purpose;
    private int purposeCatId;
    private String purposeCatShortCode;
    private PurposeCategoryDO[] purposeCategoryDOArr;
    private String primaryPurpose;
    private String termination;
    private String thirdPartyDis;
    private int thirdPartyId;
    private String thirdPartyName;
    private PiiCategoryDO[] piiCategoryArr;
    private PiiCategoryDO piiCategory;
    private String timestamp;
    private String collectionMethod;
    private String status;
    private String exactTermination;
    private String consentType;

    public PurposeDetailsDO() {

    }

    public PurposeDetailsDO(String sguid, int purposeId, String purpose, String primaryPurpose, String termination,
                            String thirdPartyDis, int thirdPartyId, String thirdPartyName, PiiCategoryDO piiCategory) {

        this.sguid = sguid;
        this.purposeId = purposeId;
        this.purpose = purpose;
        this.primaryPurpose = primaryPurpose;
        this.termination = termination;
        this.thirdPartyDis = thirdPartyDis;
        this.thirdPartyId = thirdPartyId;
        this.thirdPartyName = thirdPartyName;
        this.piiCategory = piiCategory;
    }

    public String getSGUID() {

        return sguid;
    }

    public void setSGUID(String sguid) {

        this.sguid = sguid;
    }

    public int getPurposeId() {

        return purposeId;
    }

    public void setPurposeId(int purposeId) {

        this.purposeId = purposeId;
    }

    public String getPurpose() {

        return purpose;
    }

    public void setPurpose(String purpose) {

        this.purpose = purpose;
    }

    public int getPurposeCatId() {

        return purposeCatId;
    }

    public void setPurposeCatId(int purposeCatId) {

        this.purposeCatId = purposeCatId;
    }

    public String getPurposeCatShortCode() {

        return purposeCatShortCode;
    }

    public void setPurposeCatShortCode(String purposeCatShortCode) {

        this.purposeCatShortCode = purposeCatShortCode;
    }

    public PurposeCategoryDO[] getPurposeCategoryDOArr() {

        return purposeCategoryDOArr;
    }

    public void setPurposeCategoryDOArr(PurposeCategoryDO[] purposeCategoryDOArr) {

        this.purposeCategoryDOArr = purposeCategoryDOArr;
    }

    public String getPrimaryPurpose() {

        return primaryPurpose;
    }

    public void setPrimaryPurpose(String primaryPurpose) {

        this.primaryPurpose = primaryPurpose;
    }

    public String getTermination() {

        return termination;
    }

    public void setTermination(String termination) {

        this.termination = termination;
    }

    public String getThirdPartyDis() {

        return thirdPartyDis;
    }

    public void setThirdPartyDis(String thirdPartyDis) {

        this.thirdPartyDis = thirdPartyDis;
    }

    public int getThirdPartyId() {

        return thirdPartyId;
    }

    public void setThirdPartyId(int thirdPartyId) {

        this.thirdPartyId = thirdPartyId;
    }

    public String getThirdPartyName() {

        return thirdPartyName;
    }

    public void setthirdPartyName(String thirdPartyName) {

        this.thirdPartyName = thirdPartyName;
    }

    public PiiCategoryDO[] getpiiCategoryArr() {

        return piiCategoryArr;
    }

    public void setpiiCategoryArr(PiiCategoryDO[] piiCategoryArr) {

        this.piiCategoryArr = piiCategoryArr;
    }

    public PiiCategoryDO getPiiCategory() {

        return piiCategory;
    }

    public void setPiiCategory(PiiCategoryDO piiCategory) {

        this.piiCategory = piiCategory;
    }

    public String getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(String timestamp) {

        this.timestamp = timestamp;
    }

    public String getCollectionMethod() {

        return collectionMethod;
    }

    public void setCollectionMethod(String collectionMethod) {

        this.collectionMethod = collectionMethod;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public String getExactTermination() {

        return exactTermination;
    }

    public void setExactTermination(String exactTermination) {

        this.exactTermination = exactTermination;
    }

    public String getConsentType() {

        return consentType;
    }

    public void setConsentType(String consentType) {

        this.consentType = consentType;
    }
}
