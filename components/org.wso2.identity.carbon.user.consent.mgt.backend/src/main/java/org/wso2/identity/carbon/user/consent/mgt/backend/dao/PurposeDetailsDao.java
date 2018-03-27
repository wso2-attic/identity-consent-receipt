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

package org.wso2.identity.carbon.user.consent.mgt.backend.dao;

import org.wso2.identity.carbon.user.consent.mgt.backend.exception.DataAccessException;
import org.wso2.identity.carbon.user.consent.mgt.backend.model.PurposeDetailsDO;

import java.util.List;

/**
 * Interface that contains the methods to operate on the purpose details.
 */
public interface PurposeDetailsDao {

    public int getPurposeIdByPurpose(String purposeName) throws DataAccessException;

    public List<PurposeDetailsDO> getPurposeCategories(String sguid) throws DataAccessException;

    public PurposeDetailsDO addPurposeDetails(PurposeDetailsDO purpose) throws DataAccessException;

    public PurposeDetailsDO updatePurposeDetails(PurposeDetailsDO purpose) throws DataAccessException;

    public List<PurposeDetailsDO> getPurposesForConfig() throws DataAccessException;

    public PurposeDetailsDO getPurposeByUserByService(String subjectName, int serviceId, int purposeId)
            throws DataAccessException;

    public PurposeDetailsDO deletePurpose(int purposeId) throws DataAccessException;

    public PurposeDetailsDO getPurposeDetailsById(int id) throws DataAccessException;
}
