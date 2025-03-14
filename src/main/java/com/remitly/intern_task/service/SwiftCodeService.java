package com.remitly.intern_task.service;

import com.remitly.intern_task.model.Branch;
import com.remitly.intern_task.model.Headquarter;
import com.remitly.intern_task.repository.BranchRepository;
import com.remitly.intern_task.repository.HeadquarterRepository;
import com.remitly.intern_task.requests.SwiftCodeRequest;
import com.remitly.intern_task.responses.CountrySwiftCodeDetailsResponse;
import com.remitly.intern_task.responses.SwiftCodeDetailsBranchResponse;
import com.remitly.intern_task.responses.SwiftCodeDetailsResponse;
import com.remitly.intern_task.responses.SwiftCodeDetailsResponseWithBranches;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SwiftCodeService {
    @Autowired
    private HeadquarterRepository headquarterRepository;
    @Autowired
    private BranchRepository branchRepository;

    public SwiftCodeDetailsResponseWithBranches getHeadquarterDetails(String swiftCode) {
        log.info("Fetching headquarter details for SWIFT code: {}", swiftCode);

        Headquarter headquarter = headquarterRepository.findBySwiftCode(swiftCode);
        if (headquarter == null) {
            log.error("Headquarter not found for SWIFT code: {}", swiftCode);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Headquarter not found");
        }

        log.info("Headquarter found: {} - {}", headquarter.getName(), headquarter.getSwiftCode());

        List<SwiftCodeDetailsBranchResponse> branches = branchRepository.findByHeadquarterId(headquarter).stream()
                .map(branch -> new SwiftCodeDetailsBranchResponse(
                        branch.getAddress(),
                        branch.getName(),
                        branch.getCountryIso2Code(),
                        false,
                        branch.getSwiftCode())
                )
                .collect(Collectors.toList());

        log.info("Found {} branches for headquarter with SWIFT code: {}", branches.size(), swiftCode);

        return new SwiftCodeDetailsResponseWithBranches(
                headquarter.getAddress(),
                headquarter.getName(),
                headquarter.getCountryIso2Code(),
                headquarter.getCountryName(),
                true,
                headquarter.getSwiftCode(),
                branches
        );
    }

    public SwiftCodeDetailsResponse getBranchDetails(String swiftCode) {
        log.info("Fetching branch details for SWIFT code: {}", swiftCode);

        Branch branch = branchRepository.findBySwiftCode(swiftCode);
        if (branch == null) {
            log.error("Branch not found for SWIFT code: {}", swiftCode);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found");
        }

        log.info("Branch found: {} - {}", branch.getName(), branch.getSwiftCode());

        return new SwiftCodeDetailsResponse(
                branch.getAddress(),
                branch.getName(),
                branch.getCountryIso2Code(),
                branch.getCountryName(),
                false,
                branch.getSwiftCode()
        );
    }

    public CountrySwiftCodeDetailsResponse getSwiftCodesByCountry(String countryISO2code) {
        log.info("Fetching SWIFT codes for country: {}", countryISO2code);

        List<SwiftCodeDetailsBranchResponse> swiftCodes = new ArrayList<>();

        List<Headquarter> headquarters = headquarterRepository.findByCountryIso2Code(countryISO2code);
        for (Headquarter headquarter : headquarters) {
            swiftCodes.add(new SwiftCodeDetailsBranchResponse(
                    headquarter.getAddress(),
                    headquarter.getName(),
                    headquarter.getCountryIso2Code(),
                    true,
                    headquarter.getSwiftCode()
            ));
        }

        List<Branch> branches = branchRepository.findByCountryIso2Code(countryISO2code);
        for (Branch branch : branches) {
            swiftCodes.add(new SwiftCodeDetailsBranchResponse(
                    branch.getAddress(),
                    branch.getName(),
                    branch.getCountryIso2Code(),
                    false,
                    branch.getSwiftCode()
            ));
        }

        log.info("Found {} SWIFT codes for country: {}", swiftCodes.size(), countryISO2code);

        if (!swiftCodes.isEmpty()) {
            return new CountrySwiftCodeDetailsResponse(countryISO2code, getCountryName(countryISO2code), swiftCodes);
        }

        log.warn("No SWIFT codes found for country: {}", countryISO2code);
        return null;
    }

    private String getCountryName(String countryISO2code) {
        log.info("Fetching country name for ISO2 code: {}", countryISO2code);

        Pageable pageable = PageRequest.of(0, 1);
        List<String> results = headquarterRepository.findDistinctCountryNamesByIso2Code(countryISO2code, pageable);
        if (!results.isEmpty()) {
            log.info("Country name found for ISO2 code {}: {}", countryISO2code, results.get(0));
            return results.get(0);
        }

        log.warn("No country name found for ISO2 code: {}", countryISO2code);
        return null;
    }

    @Transactional
    public String addSwiftCode(SwiftCodeRequest swiftCodeRequest) {
        log.info("Adding SWIFT code: {} for {}.", swiftCodeRequest.getSwiftCode(), swiftCodeRequest.isHeadquarter() ? "Headquarter" : "Branch");

        if (swiftCodeRequest.isHeadquarter()) {
            List<Branch> existingBranches = branchRepository.findAllBranchesBySwiftCode(swiftCodeRequest.getSwiftCode().substring(0, 8));

            if (existingBranches == null) {
                existingBranches = new ArrayList<>();
                log.info("No existing branches found for SWIFT code prefix: {}", swiftCodeRequest.getSwiftCode().substring(0, 8));
            }

            Headquarter headquarter = new Headquarter();
            headquarter.setAddress(swiftCodeRequest.getAddress());
            headquarter.setName(swiftCodeRequest.getBankName());
            headquarter.setCountryIso2Code(swiftCodeRequest.getCountryISO2().toUpperCase());
            headquarter.setCountryName(swiftCodeRequest.getCountryName().toUpperCase());
            headquarter.setSwiftCode(swiftCodeRequest.getSwiftCode());
            headquarter.setBranches(existingBranches);

            headquarterRepository.save(headquarter);

            log.info("Headquarter added with SWIFT code: {}", swiftCodeRequest.getSwiftCode());

            for (Branch branch : existingBranches) {
                branch.setHeadquarter(headquarter);
                branchRepository.save(branch);
                log.debug("Branch {} associated with headquarter.", branch.getSwiftCode());
            }
        } else {
            Headquarter headquarter = headquarterRepository.findBySwiftCode(swiftCodeRequest.getSwiftCode().substring(0, 8) + "XXX");

            if (headquarter == null) {
                log.error("No headquarter found for SWIFT code prefix: {}", swiftCodeRequest.getSwiftCode().substring(0, 8));
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Headquarter not found");
            }

            Branch branch = new Branch();
            branch.setAddress(swiftCodeRequest.getAddress());
            branch.setName(swiftCodeRequest.getBankName());
            branch.setCountryIso2Code(swiftCodeRequest.getCountryISO2().toUpperCase());
            branch.setCountryName(swiftCodeRequest.getCountryName().toUpperCase());
            branch.setSwiftCode(swiftCodeRequest.getSwiftCode());
            branch.setHeadquarter(headquarter);

            branchRepository.save(branch);

            log.info("Branch added with SWIFT code: {}", swiftCodeRequest.getSwiftCode());

            headquarter.addBranch(branch);
            headquarterRepository.save(headquarter);
        }

        return "SWIFT code added successfully.";
    }

    @Transactional
    public String deleteSwiftCode(String swiftCode) {
        log.info("Deleting SWIFT code: {}", swiftCode);

        Headquarter headquarter = headquarterRepository.findBySwiftCode(swiftCode);
        if (headquarter != null) {
            log.info("Deleting headquarter with SWIFT code: {}", swiftCode);

            List<Branch> branches = headquarter.getBranches();
            for (Branch branch : branches) {
                branch.setHeadquarter(null);
                branchRepository.save(branch);
                log.debug("Branch with SWIFT code {} disassociated from headquarter.", branch.getSwiftCode());
            }

            headquarter.setBranches(new ArrayList<>());
            headquarterRepository.save(headquarter);
            headquarterRepository.delete(headquarter);

            log.info("Headquarter with SWIFT code {} deleted successfully.", swiftCode);
            return "SWIFT code deleted successfully.";
        }

        Branch branch = branchRepository.findBySwiftCode(swiftCode);
        if (branch != null) {
            log.info("Deleting branch with SWIFT code: {}", swiftCode);

            headquarter = branch.getHeadquarter();

            if (headquarter != null) {
                headquarter.remove(branch);
                headquarterRepository.save(headquarter);
            }

            branch.setHeadquarter(null);
            branchRepository.save(branch);
            branchRepository.delete(branch);

            log.info("Branch with SWIFT code {} deleted successfully.", swiftCode);
            return "SWIFT code deleted successfully.";
        }

        log.warn("SWIFT code {} not found.", swiftCode);
        return "SWIFT code not found.";
    }
}
