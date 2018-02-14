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

package org.wso2.identity.carbon.user.consent.mgt.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(description = "")
public class ServiceInputDTO {

    private Integer serviceId = null;

    @NotNull
    private String serviceName = null;

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("serviceId")
    public Integer getServiceId() {

        return serviceId;
    }

    public void setServiceId(Integer serviceId) {

        this.serviceId = serviceId;
    }

    /**
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("serviceName")
    public String getServiceName() {

        return serviceName;
    }

    public void setServiceName(String serviceName) {

        this.serviceName = serviceName;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ServiceInputDTO {\n");

        sb.append("  serviceId: ").append(serviceId).append("\n");
        sb.append("  serviceName: ").append(serviceName).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
