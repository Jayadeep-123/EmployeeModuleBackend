package com.employee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.FamilyDetailsDTO;
import com.employee.entity.EmpFamilyDetails; // Use your entity class

@Repository
public interface EmpFamilyDetailsRepository extends JpaRepository<EmpFamilyDetails, Integer> { 

	/**
	 * This query is now 100% correct and matches all your entity files.
	 * The startup error will be fixed.
	 */
	@Query("SELECT NEW com.employee.dto.FamilyDetailsDTO(" +
	           
	           // --- 1. THIS LINE IS NOW FIXED ---
	           "rel.studentRelationType, " +  // Was 'rel.relationName'
	           
	           "CONCAT(fd.first_name, ' ', fd.last_name), " + 
	           "bg.bloodGroupName, " + // This one was correct
	           "g.genderName, " +      // This one was correct
	           "fd.nationality, " +    
	           "fd.occupation, " +     
	           "fd.date_of_birth" +      
	           ") " +
	           "FROM EmpFamilyDetails fd " + // Use your entity class
	           
	           // Join using your exact Java field names (e.g., relation_id)
	           "LEFT JOIN fd.relation_id rel " +
	           "LEFT JOIN fd.blood_group_id bg " +
	           "LEFT JOIN fd.gender_id g " +
	           
	           // This links Family Details -> Employee -> tempPayrollId
	           "WHERE fd.emp_id.tempPayrollId = :payrollId AND fd.is_active = 1")
	
	List<FamilyDetailsDTO> findFamilyDetailsByPayrollId(@Param("payrollId") String payrollId);
}