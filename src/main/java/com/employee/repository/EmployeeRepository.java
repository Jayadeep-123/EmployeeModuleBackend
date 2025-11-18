package com.employee.repository;
 
import java.util.List;
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import com.employee.entity.EmpQualification;
import com.employee.entity.Employee;
 
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
 
    @Query("SELECT e FROM Employee e WHERE e.emp_id = :id AND e.is_active = :is_active")
    Optional<Employee> findByIdAndIs_active(@Param("id") Integer id, @Param("is_active") int is_active);
    
    Optional<Employee> findByTempPayrollId(String tempPayrollId);
    
 
 
    /**
     * Finds an employee by the 'payRollId' (camelCase) field.
     * This matches the 'payRollId' field in Employee.java
     */
    Optional<Employee> findByPayRollId(String payrollId);
 
    /**
     * Finds all employees by their active status.
     * CORRECTED: Using @Query to map the clean method name
     * 'findByIsActive' to the database field 'is_active'.
     */
    @Query("SELECT e FROM Employee e WHERE e.is_active = :status")
    List<Employee> findByIsActive(@Param("status") int status);
 
    @Query("SELECT e FROM Employee e "
            + "LEFT JOIN FETCH e.campus_id c "
            + "LEFT JOIN FETCH c.city " // Fetch city for location
            + "LEFT JOIN FETCH e.employee_manager_id m " // Fetch Manager
            + "LEFT JOIN FETCH e.employee_replaceby_id r " // Fetch Replacement
            + "LEFT JOIN FETCH e.employee_hired h " // Fetch Hired By
            + "LEFT JOIN FETCH e.modeOfHiring_id moh " // Fetch Mode of Hiring
            + "LEFT JOIN FETCH e.workingMode_id wm " // Fetch Working Mode
            + "LEFT JOIN FETCH e.join_type_id jat " // Fetch Joining As
            + "WHERE e.tempPayrollId = :tempPayrollId")
       Optional<Employee> findWorkingInfoByTempPayrollId(@Param("tempPayrollId") String tempPayrollId);
    
    
    @Query("SELECT e FROM Employee e "
            + "LEFT JOIN FETCH e.qualification_id q " // The highest qualification type
            + "WHERE e.tempPayrollId = :tempPayrollId")
       Optional<Employee> findHighestQualificationDetailsByTempPayrollId(@Param("tempPayrollId") String tempPayrollId);
       
       // We also need a method to fetch the *specific* EmpQualification record
       // that corresponds to the highest qualification_id stored in the Employee table.
       // This is needed for fields like University, Institute, PassedoutYear, etc.
       @Query("SELECT eq FROM EmpQualification eq "
       	 + "JOIN eq.emp_id e "
       	 + "LEFT JOIN FETCH eq.qualification_degree_id qd "
       	 + "WHERE e.tempPayrollId = :tempPayrollId AND eq.qualification_id.qualification_id = e.qualification_id.qualification_id")
       Optional<EmpQualification> findHighestEmpQualificationRecord(@Param("tempPayrollId") String tempPayrollId);
 
}
 