package com.remitly.intern_task.service;

import com.remitly.intern_task.model.Branch;
import com.remitly.intern_task.model.Headquarter;
import com.remitly.intern_task.repository.BranchRepository;
import com.remitly.intern_task.repository.HeadquarterRepository;
import com.remitly.intern_task.requests.SwiftCodeRequest;
import com.remitly.intern_task.responses.CountrySwiftCodeDetailsResponse;
import com.remitly.intern_task.responses.SwiftCodeDetailsResponse;
import com.remitly.intern_task.responses.SwiftCodeDetailsResponseWithBranches;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class SwiftCodeServiceTest {

    @Mock
    private HeadquarterRepository headquarterRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private SwiftCodeService swiftCodeService;

    private Headquarter headquarter;
    private Branch branch;

    @BeforeEach
    void setUp() {
        headquarter = new Headquarter();
        headquarter.setSwiftCode("ABCDEFGHXXX");
        headquarter.setName("Test Bank");
        headquarter.setCountryIso2Code("PL");
        headquarter.setCountryName("Poland");
        headquarter.setAddress("Test Address");

        branch = new Branch();
        branch.setSwiftCode("ABCDEFGH123");
        branch.setName("Test Branch");
        branch.setCountryIso2Code("PL");
        branch.setCountryName("Poland");
        branch.setAddress("Branch Address");
        branch.setHeadquarter(headquarter);
    }

    @Test
    void testGetHeadquarterDetails_Success() {
        when(headquarterRepository.findBySwiftCode("ABCDEFGHXXX")).thenReturn(headquarter);
        when(branchRepository.findByHeadquarterId(headquarter)).thenReturn(new ArrayList<>());

        SwiftCodeDetailsResponseWithBranches response = swiftCodeService.getHeadquarterDetails("ABCDEFGHXXX");

        assertNotNull(response);
        assertEquals("Test Bank", response.getName());
        assertEquals("PL", response.getCountryIso2Code());
        verify(headquarterRepository, times(1)).findBySwiftCode("ABCDEFGHXXX");
    }

    @Test
    void testGetHeadquarterDetails_NotFound() {
        when(headquarterRepository.findBySwiftCode("ABCDEFGHXXX")).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> swiftCodeService.getHeadquarterDetails("ABCDEFGHXXX"));
    }

    @Test
    void testDeleteSwiftCode_DeletesHeadquarter() {
        List<Branch> branches = new ArrayList<>();
        branches.add(branch);
        headquarter.setBranches(branches);

        when(headquarterRepository.findBySwiftCode("ABCDEFGHXXX")).thenReturn(headquarter);

        String result = swiftCodeService.deleteSwiftCode("ABCDEFGHXXX");

        assertEquals("SWIFT code deleted successfully.", result);
        verify(branchRepository, times(1)).save(branch);
        verify(headquarterRepository, times(1)).delete(headquarter);
    }

    @Test
    void testDeleteSwiftCode_NotFound() {
        when(headquarterRepository.findBySwiftCode("UNKNOWN")).thenReturn(null);
        when(branchRepository.findBySwiftCode("UNKNOWN")).thenReturn(null);

        String result = swiftCodeService.deleteSwiftCode("UNKNOWN");
        assertEquals("SWIFT code not found.", result);
    }

//    @Test
//    void testGetBranchDetails_Success() {
//        when(branchRepository.findBySwiftCode("ABCDEFGH123")).thenReturn(branch);
//
//        SwiftCodeDetailsResponse response = swiftCodeService.getBranchDetails("ABCDEFGH123");
//
//        assertNotNull(response);
//        assertEquals("Test Branch", response.getName());
//        assertEquals("PL", response.getCountryIso2Code());
//        verify(branchRepository, times(1)).findBySwiftCode("ABCDEFGH123");
//    }

    @Test
    void testGetBranchDetails_NotFound() {
        when(branchRepository.findBySwiftCode("ABCDEFGH123")).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> swiftCodeService.getBranchDetails("ABCDEFGH123"));
    }

    @Test
    void testGetSwiftCodesByCountry_Success() {
        List<Headquarter> headquarters = new ArrayList<>();
        headquarters.add(headquarter);
        Pageable pageable = PageRequest.of(0, 1);

        List<Branch> branches = new ArrayList<>();
        branches.add(branch);

        when(headquarterRepository.findByCountryIso2Code("PL")).thenReturn(headquarters);
        when(branchRepository.findByCountryIso2Code("PL")).thenReturn(branches);
        when(headquarterRepository.findDistinctCountryNamesByIso2Code("PL", pageable)).thenReturn(List.of("Poland"));

        CountrySwiftCodeDetailsResponse response = swiftCodeService.getSwiftCodesByCountry("PL");

        assertNotNull(response);
        assertEquals("PL", response.getCountryISO2());
        assertEquals("Poland", response.getCountryName());
        assertEquals(2, response.getSwiftCodes().size());
    }

    @Test
    void testGetSwiftCodesByCountry_NoData() {
        when(headquarterRepository.findByCountryIso2Code("XX")).thenReturn(new ArrayList<>());
        when(branchRepository.findByCountryIso2Code("XX")).thenReturn(new ArrayList<>());

        CountrySwiftCodeDetailsResponse response = swiftCodeService.getSwiftCodesByCountry("XX");

        assertNull(response);
    }

    @Test
    void testAddSwiftCode_Headquarter() {
        SwiftCodeRequest request = new SwiftCodeRequest();
        request.setSwiftCode("ABCDEFGHXXX");
        request.setAddress("New Address");
        request.setBankName("New Bank");
        request.setCountryISO2("PL");
        request.setCountryName("Poland");
        request.setHeadquarter(true);

        when(branchRepository.findAllBranchesBySwiftCode("ABCDEFGH")).thenReturn(new ArrayList<>());

        String result = swiftCodeService.addSwiftCode(request);

        assertEquals("SWIFT code added successfully.", result);
        verify(headquarterRepository, times(1)).save(any(Headquarter.class));
    }

    @Test
    void testAddSwiftCode_Branch() {
        SwiftCodeRequest request = new SwiftCodeRequest();
        request.setSwiftCode("ABCDEFGH123");
        request.setAddress("Branch Address");
        request.setBankName("Test Branch");
        request.setCountryISO2("PL");
        request.setCountryName("Poland");
        request.setHeadquarter(false);

        when(headquarterRepository.findBySwiftCode("ABCDEFGHXXX")).thenReturn(headquarter);

        String result = swiftCodeService.addSwiftCode(request);

        assertEquals("SWIFT code added successfully.", result);
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    void testAddSwiftCode_HeadquarterWithExistingBranches() {
        List<Branch> existingBranches = new ArrayList<>();
        Branch existingBranch = new Branch();
        existingBranch.setSwiftCode("ABCDEFGH123");
        existingBranches.add(existingBranch);

        SwiftCodeRequest request = new SwiftCodeRequest();
        request.setSwiftCode("ABCDEFGHXXX");
        request.setAddress("New HQ Address");
        request.setBankName("Test HQ");
        request.setCountryISO2("PL");
        request.setCountryName("Poland");
        request.setHeadquarter(true);

        when(branchRepository.findAllBranchesBySwiftCode("ABCDEFGH")).thenReturn(existingBranches);

        String result = swiftCodeService.addSwiftCode(request);

        assertEquals("SWIFT code added successfully.", result);
        verify(headquarterRepository, times(1)).save(any(Headquarter.class));
    }

    @Test
    void testDeleteSwiftCode_BranchOnly() {
        when(branchRepository.findBySwiftCode("ABCDEFGH123")).thenReturn(branch);
        when(headquarterRepository.findBySwiftCode("ABCDEFGH123")).thenReturn(null);

        String result = swiftCodeService.deleteSwiftCode("ABCDEFGH123");

        assertEquals("SWIFT code deleted successfully.", result);
        verify(branchRepository, times(1)).delete(branch);
        verify(headquarterRepository, times(1)).save(headquarter);
    }
}