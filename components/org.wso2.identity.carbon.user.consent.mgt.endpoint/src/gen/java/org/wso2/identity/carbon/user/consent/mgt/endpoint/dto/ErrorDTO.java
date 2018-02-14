package org.wso2.identity.carbon.user.consent.mgt.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class ErrorDTO {

    private String error = null;

    private String errorDescription = null;

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("error")
    public String getError() {

        return error;
    }

    public void setError(String error) {

        this.error = error;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("errorDescription")
    public String getErrorDescription() {

        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {

        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorDTO {\n");

        sb.append("  error: ").append(error).append("\n");
        sb.append("  errorDescription: ").append(errorDescription).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
