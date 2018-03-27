package org.wso2.identity.carbon.user.consent.mgt.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class ErrorDTO {

    private String errorCode = null;

    private String errorDescription = null;

    private String errorCause = null;

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("error")
    public String getErrorCode() {

        return errorCode;
    }

    public void setErrorCode(String errorCode) {

        this.errorCode = errorCode;
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

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("errorCause")
    public String getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(String errorCause){
        this.errorCause=errorCause;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorDTO {\n");

        sb.append("  errorCode: ").append(errorCode).append("\n");
        sb.append("  errorDescription: ").append(errorDescription).append("\n");
        sb.append("  errorCause: ").append(errorCause).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
