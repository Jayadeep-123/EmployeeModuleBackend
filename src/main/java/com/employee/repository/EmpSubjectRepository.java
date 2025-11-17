package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.CategoryInfoDTO;
import com.employee.entity.EmpSubject;

@Repository
public interface EmpSubjectRepository extends JpaRepository<EmpSubject, Integer> {
	
	
	@Query("SELECT NEW com.employee.dto.CategoryInfoDTO(" +
	           "et.empType, " +            // empTypeName
	           "COALESCE(s.subjectName, null), " +
	           "d.departmentName, " +
	           "des.designationName, " +
	           "COALESCE(sub.agreeNoPeriod, null)" +
	       ") " +
	       "FROM Employee e " +
	       "JOIN e.employeeTypeId et " +
	       "JOIN e.department d " +
	       "JOIN e.designation des " +
	       "LEFT JOIN EmpSubject sub ON sub.empId = e " +
	       "LEFT JOIN sub.subjectId s " +
	       "WHERE e.tempPayrollId = :payrollId " +
	       "AND e.isActive = 1 " +
	       "AND et.isActive = 1")
	List<CategoryInfoDTO> findCategoryInfoByPayrollId(@Param("payrollId") String payrollId);
	
	  @Query("SELECT es FROM EmpSubject es WHERE es.empId.empId = :empId AND es.isActive = 1")
	    List<EmpSubject> findActiveSubjectsByEmpId(@Param("empId") Integer empId);
}

