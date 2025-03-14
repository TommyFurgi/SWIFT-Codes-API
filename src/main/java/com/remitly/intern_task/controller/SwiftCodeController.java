package com.remitly.intern_task.controller;


import com.remitly.intern_task.requests.SwiftCodeRequest;
import com.remitly.intern_task.responses.CountrySwiftCodeDetailsResponse;
import com.remitly.intern_task.responses.SwiftCodeDetailsResponse;
import com.remitly.intern_task.service.SwiftCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/v1/swift-codes")
public class SwiftCodeController {
    @Autowired
    private SwiftCodeService swiftCodeService;

    @GetMapping(value = "/{swiftCode}")
    public ResponseEntity<?> getSwiftCodeDetails(@PathVariable String swiftCode) {
        try {
            if (swiftCode.endsWith("XXX")) {
                SwiftCodeDetailsResponse response = swiftCodeService.getHeadquarterDetails(swiftCode);
                return ResponseEntity.ok(response);
            } else {
                SwiftCodeDetailsResponse response = swiftCodeService.getBranchDetails(swiftCode);
                return ResponseEntity.ok(response);
            }
        } catch (ResponseStatusException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "Unexpected error occurred", "details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping(value = "/country/{countryISO2code}")
    public ResponseEntity<?> getSwiftCodesByCountry(@PathVariable String countryISO2code) {
        try {
            CountrySwiftCodeDetailsResponse response = swiftCodeService.getSwiftCodesByCountry(countryISO2code);

            if (response != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No data found for the specified country"));
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "Unexpected error occurred", "details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addSwiftCode(@RequestBody SwiftCodeRequest swiftCodeRequest) {
        try {
            String responseMessage = swiftCodeService.addSwiftCode(swiftCodeRequest);
            Map<String, String> response = Map.of("message", responseMessage);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "Unexpected error occurred", "details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{swiftCode}")
    public ResponseEntity<Map<String, String>> deleteSwiftCode(@PathVariable String swiftCode) {
        try {
            String responseMessage = swiftCodeService.deleteSwiftCode(swiftCode);
            Map<String, String> response = Map.of("message", responseMessage);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "Unexpected error occurred", "details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
