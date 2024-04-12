package com.ecommerce.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.Models.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
	
	@Query("SELECT a FROM Address a Where a.firstName = :firstName AND a.lastName = :lastName AND a.streetAddress = :streetAddress AND a.city = :city AND a.state = :state AND a.zipCode = :zipCode AND a.mobile = :mobile")
	public List<Address> isAddressExist(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("streetAddress") String streetAddress, @Param("city") String city, @Param("state") String state, @Param("zipCode") String zipCode, @Param("mobile") String mobile);
	
}
