package com.vm.customerservice.service;

import com.vm.customerservice.Exception.AccessDeniedException;
import com.vm.customerservice.Exception.CustomerNotFoundException;
import com.vm.customerservice.Exception.EmailAlreadyExistsException;
import com.vm.customerservice.Mapper.CustomerMapper;
import com.vm.customerservice.dto.CustomerRequestDTO;
import com.vm.customerservice.dto.CustomerResponseDTO;
//import com.vm.customerservice.grpc.BillingServiceGrpcClient;
//import com.vm.customerservice.kafka.KafkaProducer;
import com.vm.customerservice.model.Customer;
import com.vm.customerservice.repository.CustomerRepository;
import com.vm.customerservice.security.RequestIdentity;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
//    private final BillingServiceGrpcClient billingServiceGrpcClient;
//    private final KafkaProducer kafkaProducer;

    public CustomerService(CustomerRepository customerRepository
//                          BillingServiceGrpcClient billingServiceGrpcClient,
//                          KafkaProducer kafkaProducer
    ) {
        this.customerRepository = customerRepository;
//        this.billingServiceGrpcClient = billingServiceGrpcClient;
//        this.kafkaProducer = kafkaProducer;
    }

    public List<CustomerResponseDTO> getCustomers(RequestIdentity requestIdentity) {
        requireAdmin(requestIdentity, "Only administrators can list all customers");
        List<Customer> customers = customerRepository.findAll();

        return customers.stream().map(CustomerMapper::toDTO).toList();
    }

    public CustomerResponseDTO getCurrentCustomer(RequestIdentity requestIdentity) {
        Customer customer = customerRepository.findByEmail(requestIdentity.email())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for email: " + requestIdentity.email()));
        return CustomerMapper.toDTO(customer);
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO, RequestIdentity requestIdentity) {
        if (!requestIdentity.isAdmin() && !requestIdentity.email().equalsIgnoreCase(customerRequestDTO.getEmail())) {
            log.warn("Denied customer profile creation for authenticated email={} and payload email={}",
                    requestIdentity.email(), customerRequestDTO.getEmail());
            throw new AccessDeniedException("Customers can only create their own profile");
        }

        if (customerRepository.existsByEmail(customerRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "A customer with this email " + "already exists"
                            + customerRequestDTO.getEmail());
        }

        Customer newCustomer = customerRepository.save(
                CustomerMapper.toModel(customerRequestDTO));

//        billingServiceGrpcClient.createBillingAccount(newCustomer.getId().toString(),
//                newCustomer.getName(), newCustomer.getEmail());
//
//        kafkaProducer.sendEvent(newCustomer);

        return CustomerMapper.toDTO(newCustomer);
    }

    public CustomerResponseDTO updateCurrentCustomer(CustomerRequestDTO customerRequestDTO,
                                                     RequestIdentity requestIdentity) {
        Customer customer = customerRepository.findByEmail(requestIdentity.email())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for email: " + requestIdentity.email()));

        if (!requestIdentity.email().equalsIgnoreCase(customerRequestDTO.getEmail())) {
            throw new AccessDeniedException("Customers cannot change their profile email through this endpoint");
        }

        customer.setName(customerRequestDTO.getName());
        customer.setAddress(customerRequestDTO.getAddress());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setDateOfBirth(LocalDate.parse(customerRequestDTO.getDateOfBirth()));

        Customer updatedCustomer = customerRepository.save(customer);
        return CustomerMapper.toDTO(updatedCustomer);
    }

    public CustomerResponseDTO updateCustomer(UUID id,
                                              CustomerRequestDTO customerRequestDTO,
                                              RequestIdentity requestIdentity) {
        requireAdmin(requestIdentity, "Only administrators can update arbitrary customer records");

        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with ID: " + id));

        if (customerRepository.existsByEmailAndIdNot(customerRequestDTO.getEmail(),
                id)) {
            throw new EmailAlreadyExistsException(
                    "A customer with this email " + "already exists"
                            + customerRequestDTO.getEmail());
        }

        customer.setName(customerRequestDTO.getName());
        customer.setAddress(customerRequestDTO.getAddress());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setDateOfBirth(LocalDate.parse(customerRequestDTO.getDateOfBirth()));

        Customer updatedCustomer = customerRepository.save(customer);
        return CustomerMapper.toDTO(updatedCustomer);
    }

    public void deleteCustomer(UUID id, RequestIdentity requestIdentity) {
        requireAdmin(requestIdentity, "Only administrators can delete customers");
        customerRepository.deleteById(id);
    }

    private void requireAdmin(RequestIdentity requestIdentity, String message) {
        if (!requestIdentity.isAdmin()) {
            throw new AccessDeniedException(message);
        }
    }
}
