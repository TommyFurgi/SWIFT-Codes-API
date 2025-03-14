package com.remitly.intern_task.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SwiftCodeDetailsResponse {
    @JsonProperty("address")
    private String address;

    @JsonProperty("bankName")
    private String name;

    @JsonProperty("countryISO2")
    private String countryIso2Code;

    @JsonProperty("countryName")
    private String countryName;

    @JsonProperty("isHeadquarter")
    private boolean headquarter;

    @JsonProperty("swiftCode")
    private String swiftCode;
}
