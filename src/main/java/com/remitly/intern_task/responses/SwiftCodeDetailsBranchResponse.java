package com.remitly.intern_task.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SwiftCodeDetailsBranchResponse {
    @JsonProperty("address")
    private String address;

    @JsonProperty("bankName")
    private String name;

    @JsonProperty("countryISO2")
    private String countryIso2Code;

    @JsonProperty("isHeadquarter")
    private boolean headquarter;

    @JsonProperty("swiftCode")
    private String swiftCode;
}
