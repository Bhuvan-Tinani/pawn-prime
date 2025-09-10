package com.project.pawnprime.controller;

import com.project.pawnprime.dto.customerDTO.CustomerDTO;
import com.project.pawnprime.mapper.CustomerMapper;
import com.project.pawnprime.model.Agent;
import com.project.pawnprime.model.Customer;
import com.project.pawnprime.model.CustomerAddress;
import com.project.pawnprime.service.AgentService;
import com.project.pawnprime.service.CustomerService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService customerService;
	private final AgentService agentService;

	public CustomerController(CustomerService customerService, AgentService agentService) {
		this.customerService = customerService;
		this.agentService = agentService;
	}

	// Create customer for a given agent (agentId in path; also allowed in body for
	// clarity)

	@PreAuthorize("hasRole('AGENT')")
	@PostMapping(value = "/{agentId}", consumes = {"multipart/form-data"})
	public ResponseEntity<String> createCustomer(
	        @PathVariable Long agentId,
	        @RequestPart("customer") CustomerDTO customerDTO,
	        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
	        @RequestPart(value = "aadharImage", required = false) MultipartFile aadharImage) {

	    // ✅ Ensure DTO agentId matches path agentId
	    if (customerDTO.getAgentId() != null && !customerDTO.getAgentId().equals(agentId)) {
	        return ResponseEntity.badRequest().build();
	    }

	    // ✅ Convert DTO → Entity
	    Customer customer = CustomerMapper.toEntity(customerDTO);

	    // ✅ Set address from DTO
	    CustomerAddress cusAddr = new CustomerAddress();
	    cusAddr.setLine1(customerDTO.getAdrLine1());
	    cusAddr.setLine2(customerDTO.getAdrLine2());
	    cusAddr.setPincode(customerDTO.getPincode());
	    cusAddr.setCity(customerDTO.getCity());
	    cusAddr.setState(customerDTO.getState());
	    cusAddr.setCountry(customerDTO.getCountry());
	    cusAddr.setCustomer(customer);

	    List<CustomerAddress> cusAddres = new ArrayList<>();
	    cusAddres.add(cusAddr);
	    customer.setAddresses(cusAddres);

	    // ✅ Save with images
	    Customer saved = customerService.createCustomer(customer, agentId, profileImage, aadharImage);

	    return ResponseEntity.ok("Customer details created successfully!");
	}


	@PreAuthorize("hasRole('AGENT','ADMIN')")
	@GetMapping
	public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
		List<CustomerDTO> customers = customerService.getAllCustomers().stream().map(CustomerMapper::toDTO).toList();
		return ResponseEntity.ok(customers);
	}

//    @GetMapping
//    public String getAllCustomers() {
//        return "hello";
//    }

	@PreAuthorize("hasRole('AGENT','ADMIN')")
	@GetMapping("agent/{agentId}")
	public ResponseEntity<List<CustomerDTO>> getCustomersByAgent(@PathVariable Long agentId) {
		List<CustomerDTO> customers = customerService.getCustomersByAgent(agentId).stream().map(CustomerMapper::toDTO)
				.toList();
		return ResponseEntity.ok(customers);
	}

	@PreAuthorize("hasRole('AGENT','ADMIN')")
	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long customerId) {
		Customer customer = customerService.getCustomerById(customerId);
		return ResponseEntity.ok(CustomerMapper.toDTO(customer));
	}
	
	@PreAuthorize("hasRole('AGENT','ADMIN')")
	@GetMapping("/search/aadhar/{aadharNo}")
	public ResponseEntity<CustomerDTO> getCustomerByAadhar(@PathVariable String aadharNo) {
	    return customerService.getCustomerByAadharNo(aadharNo)
	            .map(customer -> ResponseEntity.ok(CustomerMapper.toDTO(customer)))
	            .orElse(ResponseEntity.notFound().build());
	}


}
