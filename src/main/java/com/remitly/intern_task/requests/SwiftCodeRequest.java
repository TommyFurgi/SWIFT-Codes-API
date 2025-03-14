package com.remitly.intern_task.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwiftCodeRequest {
    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter;
    private String swiftCode;
}
