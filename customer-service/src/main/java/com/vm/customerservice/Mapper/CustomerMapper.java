package com.vm.customerservice.Mapper;


import com.vm.customerservice.dto.CustomerRequestDTO;
import com.vm.customerservice.dto.CustomerResponseDTO;
import com.vm.customerservice.model.Customer;
import java.time.LocalDate;

public class CustomerMapper {
    public static CustomerResponseDTO toDTO(Customer patient) {
        CustomerResponseDTO patientDTO = new CustomerResponseDTO();
        patientDTO.setId(patient.getId().toString());
        patientDTO.setName(patient.getName());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setEmail(patient.getEmail());
        patientDTO.setDateOfBirth(patient.getDateOfBirth().toString());

        return patientDTO;
    }

    public static Customer toModel(CustomerRequestDTO patientRequestDTO) {
        Customer patient = new Customer();
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        return patient;
    }
}