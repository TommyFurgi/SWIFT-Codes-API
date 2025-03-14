package com.remitly.intern_task.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.remitly.intern_task.model.Branch;
import com.remitly.intern_task.model.Headquarter;
import com.remitly.intern_task.repository.BranchRepository;
import com.remitly.intern_task.repository.HeadquarterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class SwiftCodeParser {
    @Autowired
    private HeadquarterRepository headquarterRepository;

    @Autowired
    private BranchRepository branchRepository;


    public List<String[]> readFile(String filePath) {
        List<String[]> records = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            log.info("Reading CSV file from path: {}", filePath);
            String[] nextLine;
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                records.add(nextLine);
            }
            log.info("Successfully read {} records from the CSV file.", records.size());
        } catch (IOException | CsvValidationException e) {
            log.error("Error reading CSV file: {}", e.getMessage(), e);
        }
        return records;
    }

    public Map<String, Headquarter> parseData(List<String[]> records) {
        log.info("Parsing data from {} records.", records.size());
        Map<String, Headquarter> headquartersMap = new HashMap<>();

        for (String[] data : records) {
            String countryIso2Code = data[0].toUpperCase();
            String swiftCode = data[1];
            String codeType = data[2];
            String name = data[3];
            String address = data[4];
            String townName = data[5];
            String countryName = data[6];
            String timeZone = data[7];

            log.debug("Processing swiftCode: {}, countryIso2Code: {}", swiftCode, countryIso2Code);

            if (swiftCode.endsWith("XXX")) {
                Headquarter headquarter = Headquarter.builder()
                        .countryIso2Code(countryIso2Code)
                        .swiftCode(swiftCode)
                        .codeType(codeType)
                        .name(name)
                        .address(address)
                        .townName(townName)
                        .countryName(countryName)
                        .timeZone(timeZone)
                        .build();

                headquartersMap.put(swiftCode.substring(0, 8), headquarter);
                log.debug("Headquarter added with swiftCode prefix: {}", swiftCode.substring(0, 8));
            }
        }

        log.info("Parsed {} headquarters.", headquartersMap.size());
        return headquartersMap;
    }

    @Transactional
    public void saveData(List<String[]> records, Map<String, Headquarter> headquartersMap) {
        log.info("Saving headquarters data to the database.");
        headquarterRepository.saveAll(headquartersMap.values());
        log.info("Saved {} headquarters.", headquartersMap.size());

        int branchCount = 0;
        for (String[] data : records) {
            String swiftCode = data[1];
            if (!swiftCode.endsWith("XXX")) {
                String hqSwiftPrefix = swiftCode.substring(0, 8);
                Headquarter headquarter = headquartersMap.get(hqSwiftPrefix);

                if (headquarter != null) {
                    Branch branch = Branch.builder()
                            .swiftCode(swiftCode)
                            .countryIso2Code(data[0])
                            .codeType(data[2])
                            .name(data[3])
                            .address(data[4])
                            .townName(data[5])
                            .countryName(data[6])
                            .timeZone(data[7])
                            .headquarter(headquarter)
                            .build();

                    branchRepository.save(branch);
                    branchCount++;
                    log.debug("Saved branch with swiftCode: {}", swiftCode);
                } else {
                    log.warn("No matching headquarter found for branch with swiftCode: {}", swiftCode);
                }
            }
        }

        log.info("Saved {} branches.", branchCount);
    }

    public void processSwiftCodes(String filePath) {
        log.info("Starting to process swift codes from file: {}", filePath);

        List<String[]> records = readFile(filePath);
        if (records.isEmpty()) {
            log.warn("No records to process from file: {}", filePath);
            return;
        }

        Map<String, Headquarter> headquartersMap = parseData(records);
        saveData(records, headquartersMap);

        log.info("Processing of swift codes completed.");
    }
}
