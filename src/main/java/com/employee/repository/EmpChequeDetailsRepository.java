package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpChequeDetails;
import com.employee.entity.Employee;

@Repository
public interface EmpChequeDetailsRepository extends JpaRepository<EmpChequeDetails, Integer> {

	Optional<EmpChequeDetails> findByEmpChequeDetailsIdAndIsActive(Integer empChequeDetailsId, int isActive);

List<EmpChequeDetails> findByEmployeeEmpId(int empId);
	
	@Query("SELECT e FROM EmpChequeDetails e WHERE e.employee.empId = :empId AND e.isActive = 1")
    List<EmpChequeDetails> findActiveChequesByEmpId(@Param("empId") Integer empId);
}