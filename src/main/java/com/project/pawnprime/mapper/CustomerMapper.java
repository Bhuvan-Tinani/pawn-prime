package com.project.pawnprime.mapper;


import com.project.pawnprime.dto.customerDTO.CustomerDTO;
import com.project.pawnprime.model.Customer;
import com.project.pawnprime.model.CustomerAddress;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerMapper {
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setMiddleName(dto.getMiddleName());
        customer.setLastName(dto.getLastName());
        customer.setMobile(dto.getMobile());
        customer.setAadharNo(dto.getAadharNo());
        if (dto.getDob() != null) {
        	
        	CharSequence dobAsCharSeq = dto.getDob().toString();
        	LocalDate date=LocalDate.parse(dobAsCharSeq, formatter);
        	customer.setDob(date);
        }
        return customer; // agent linkage handled in service using agentId
    }
    
    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) return null;

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setMiddleName(customer.getMiddleName());
        dto.setLastName(customer.getLastName());
        dto.setMobile(customer.getMobile());
        dto.setAadharNo(customer.getAadharNo());

        if (customer.getDob() != null) {
            dto.setDob(customer.getDob()); // LocalDate → String
        }

        if (customer.getCreatedBy() != null) {
            dto.setAgentId(customer.getCreatedBy().getId()); // include agentId
        }
        
        List<CustomerAddress> addresses=customer.getAddresses();
        for(CustomerAddress adr:addresses) {
        	dto.setAdrLine1(adr.getLine1());
        	dto.setAdrLine2(adr.getLine2());
        	dto.setCity(adr.getCity());
        	dto.setState(adr.getState());
        	dto.setCountry(adr.getCountry());
        	dto.setPincode(adr.getPincode());
        }

        return dto;
    }
}
