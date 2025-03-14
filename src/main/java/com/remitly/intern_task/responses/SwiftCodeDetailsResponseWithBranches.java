package com.remitly.intern_task.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.remitly.intern_task.model.Branch;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
public class SwiftCodeDetailsResponseWithBranches extends SwiftCodeDetailsResponse {
    @JsonProperty("branches")
    private List<SwiftCodeDetailsBranchResponse> branches;

    public SwiftCodeDetailsResponseWithBranches(String address, String name, String countryIso2Code,
                                                String countryName, boolean isHeadquarter, String swiftCode,
                                                List<SwiftCodeDetailsBranchResponse> branches) {
        super(address, name, countryIso2Code, countryName, isHeadquarter, swiftCode);
        this.branches = branches;
    }
}
