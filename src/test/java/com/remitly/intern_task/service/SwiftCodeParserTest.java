package com.remitly.intern_task.service;

import com.remitly.intern_task.model.Headquarter;
import com.remitly.intern_task.repository.BranchRepository;
import com.remitly.intern_task.repository.HeadquarterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwiftCodeParserTest {

    @Mock
    private HeadquarterRepository headquarterRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private SwiftCodeParser swiftCodeParser;

    private List<String[]> sampleRecords;

    @BeforeEach
    void setUp() {
        sampleRecords = new ArrayList<>();
        sampleRecords.add(new String[]{"PL", "ABCDEFGHXXX", "HQ", "Test Bank", "Test Address", "Warsaw", "Poland", "UTC+1"});
        sampleRecords.add(new String[]{"PL", "ABCDEFGH123", "Branch", "Test Branch", "Branch Address", "Warsaw", "Poland", "UTC+1"});
    }

    @Test
    void testParseData() {
        Map<String, Headquarter> result = swiftCodeParser.parseData(sampleRecords);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("ABCDEFGH"));
        assertEquals("Test Bank", result.get("ABCDEFGH").getName());
    }

    @Test
    void testSaveData() {
        Map<String, Headquarter> headquartersMap = swiftCodeParser.parseData(sampleRecords);
        swiftCodeParser.saveData(sampleRecords, headquartersMap);

        verify(headquarterRepository, times(1)).saveAll(any());
        verify(branchRepository, times(1)).save(any());
    }

    @Test
    void testParseData_NoHeadquarterData() {
        sampleRecords.clear();
        sampleRecords.add(new String[]{"PL", "ABCDEFGH123", "Branch", "Test Branch", "Branch Address", "Warsaw", "Poland", "UTC+1"});

        Map<String, Headquarter> result = swiftCodeParser.parseData(sampleRecords);
        assertEquals(0, result.size());
    }

    @Test
    void testParseData_MultipleHeadquarters() {
        sampleRecords.clear();
        sampleRecords.add(new String[]{"PL", "ABCDEFGHXXX", "HQ", "Test Bank", "Test Address", "Warsaw", "Poland", "UTC+1"});
        sampleRecords.add(new String[]{"PL", "HIJKLMNDXXX", "HQ", "Another Bank", "Another Address", "Gdansk", "Poland", "UTC+1"});

        Map<String, Headquarter> result = swiftCodeParser.parseData(sampleRecords);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("ABCDEFGH"));
        assertTrue(result.containsKey("HIJKLMND"));
    }

    @Test
    void testProcessSwiftCodes_EmptyData() {
        List<String[]> emptyRecords = new ArrayList<>();
        Map<String, Headquarter> headquartersMap = swiftCodeParser.parseData(emptyRecords);
        assertTrue(headquartersMap.isEmpty());
    }
}
