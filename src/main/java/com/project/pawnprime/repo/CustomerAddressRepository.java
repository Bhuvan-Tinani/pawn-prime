package com.project.pawnprime.repo;

import com.project.pawnprime.model.CustomerAddress;
import com.project.pawnprime.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

    // find all addresses of a customer
    List<CustomerAddress> findByCustomer(Customer customer);

    // OR if you only have customerId
    List<CustomerAddress> findByCustomer_Id(Long customerId);

    // find by city
    List<CustomerAddress> findByCity(String city);

    // find by pincode
    List<CustomerAddress> findByPincode(String pincode);
}
