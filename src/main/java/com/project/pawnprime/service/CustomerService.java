package com.project.pawnprime.service;

import com.project.pawnprime.model.Agent;
import com.project.pawnprime.model.Customer;
import com.project.pawnprime.model.CustomerAddress;
import com.project.pawnprime.repo.AgentRepository;
import com.project.pawnprime.repo.CustomerAddressRepository;
import com.project.pawnprime.repo.CustomerRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final AgentRepository agentRepository;
	private final CustomerAddressRepository cusAddrRepo;
	private final ImageUploadService imageUploadService;

	public CustomerService(CustomerRepository customerRepository, AgentRepository agentRepository,
			CustomerAddressRepository cusAddrRepo, ImageUploadService imageUploadService) {
		this.customerRepository = customerRepository;
		this.agentRepository = agentRepository;
		this.cusAddrRepo = cusAddrRepo;
		this.imageUploadService = imageUploadService;
	}

	// Create new customer
	public Customer createCustomer(Customer customer, Long agentId, MultipartFile profileImage,
			MultipartFile aadharImage) {
		Agent agent = agentRepository.findById(agentId).orElseThrow(() -> new RuntimeException("Agent not found"));
		customer.setCreatedBy(agent);
		try {
			if (profileImage != null) {
				customer.setPhotoUrl(imageUploadService.uploadFile(profileImage));
			}
			if(aadharImage!=null) {
				customer.setAadharUrl(imageUploadService.uploadFile(aadharImage));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return customerRepository.save(customer);
	}

	// Get all customers
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	// Get customers by agent
	public List<Customer> getCustomersByAgent(Long agentId) {
		return customerRepository.findAll().stream().filter(c -> c.getCreatedBy().getId().equals(agentId)).toList();
	}

	public CustomerAddress savedCustomerAddress(CustomerAddress cusAdr) {
		return cusAddrRepo.save(cusAdr);
	}

	public Customer getCustomerById(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));
	}

	public Optional<Customer> getCustomerByAadharNo(String aadharNo) {
		return customerRepository.findByAadharNo(aadharNo);
	}

}
