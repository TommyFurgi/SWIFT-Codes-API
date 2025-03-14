package com.remitly.intern_task.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CountrySwiftCodeDetailsResponse {
    @JsonProperty("countryISO2")
    private String countryISO2;
    @JsonProperty("countryName")
    private String countryName;
    @JsonProperty("swiftCodes")
    private List<SwiftCodeDetailsBranchResponse> swiftCodes;
}

