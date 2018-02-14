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
import org.wso2.identity.carbon.user.consent.mgt.backend.model.ServicesDO;

import java.util.List;

/**
 * Interface that contains the methods to operate on the service details.
 */
public interface ServicesDao {

    public List<ServicesDO> getServices();

    public int getServiceIdByService(String serviceName) throws DataAccessException;

    public ServicesDO addServiceDetails(ServicesDO service) throws DataAccessException;

    public ServicesDO updateServiceDetails(ServicesDO service) throws DataAccessException;

    public List<ServicesDO> getServicesForConf() throws DataAccessException;

    public List<ServicesDO> getServicesForUserView(String subjectName) throws DataAccessException;

    public List<ServicesDO> getServiceDetailsByThirdParty(String subjectName, int thirdPartyId)
            throws DataAccessException;

    public ServicesDO getServiceByUserByServiceId(String subjectName, int serviceId) throws DataAccessException;

    public ServicesDO deleteService(int serviceId) throws DataAccessException;

    public ServicesDO getServiceById(int id) throws DataAccessException;
}
