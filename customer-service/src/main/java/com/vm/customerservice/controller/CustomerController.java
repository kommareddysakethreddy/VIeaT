package com.vm.customerservice.controller;

import com.vm.customerservice.dto.CustomerRequestDTO;
import com.vm.customerservice.dto.CustomerResponseDTO;
import com.vm.customerservice.dto.validators.CreateCustomerValidationGroup;
import com.vm.customerservice.security.RequestIdentity;
import com.vm.customerservice.security.RequestIdentityResolver;
import com.vm.customerservice.service.CustomerService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.groups.Default;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients")
//@Tag(name = "Customer", description = "API for managing Customers")
public class CustomerController {

    private final CustomerService patientService;
    private final RequestIdentityResolver requestIdentityResolver;

    public CustomerController(CustomerService patientService, RequestIdentityResolver requestIdentityResolver) {
        this.patientService = patientService;
        this.requestIdentityResolver = requestIdentityResolver;
    }

    @GetMapping
    //@Operation(summary = "Get Customers")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomers(HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        List<CustomerResponseDTO> patients = patientService.getCustomers(requestIdentity);
        return ResponseEntity.ok().body(patients);
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponseDTO> getCurrentCustomer(HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok(patientService.getCurrentCustomer(requestIdentity));
    }

    @PostMapping
    //@Operation(summary = "Create a new Customer")
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Validated({Default.class, CreateCustomerValidationGroup.class})
            @RequestBody CustomerRequestDTO patientRequestDTO,
            HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);

        CustomerResponseDTO patientResponseDTO = patientService.createCustomer(
                patientRequestDTO, requestIdentity);

        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerResponseDTO> updateCurrentCustomer(
            @Validated({Default.class}) @RequestBody CustomerRequestDTO patientRequestDTO,
            HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok().body(patientService.updateCurrentCustomer(patientRequestDTO, requestIdentity));
    }

    @PutMapping("/{id}")
    //@Operation(summary = "Update a new Customer")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable UUID id,
                                                              @Validated({Default.class}) @RequestBody CustomerRequestDTO patientRequestDTO,
                                                              HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);

        CustomerResponseDTO patientResponseDTO = patientService.updateCustomer(id,
                patientRequestDTO, requestIdentity);

        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @DeleteMapping("/{id}")
   // @Operation(summary = "Delete a Customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id, HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        patientService.deleteCustomer(id, requestIdentity);
        return ResponseEntity.noContent().build();
    }
}
