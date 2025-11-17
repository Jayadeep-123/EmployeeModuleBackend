package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.WorkingInfoDTO;
import com.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query("SELECT e FROM Employee e WHERE e.emp_id = :id AND e.is_active = :is_active")
    Optional<Employee> findByIdAndIs_active(@Param("id") Integer id, @Param("is_active") int is_active);
    
    Optional<Employee> findByTempPayrollId(String tempPayrollId);
    
    @Query("SELECT NEW com.employee.dto.WorkingInfoDTO(" +
               "c.campusName, " +         // campus
               "null AS building, " +     // building (still null)
               "CONCAT(re.first_name, ' ', re.last_name), " + // replacementEmployee
               "c.cmps_code, " +          // campusCode
               "CONCAT(m.first_name, ' ', m.last_name), " +   // manager
               "moh.mode_of_hiring_name, " + // modeOfHiring
               "c.cmps_type, " +             // campusType
               "wm.work_mode_type, " +       // workingMode
               "CONCAT(hb.first_name, ' ', hb.last_name), " + // hiredBy
               "ci.cityName, " +             // location
               "ja.join_type, " +            // joiningAs
               "emp.date_of_join" +          // joiningDate
               ") " +
               "FROM Employee emp " + 
               "LEFT JOIN emp.campus_id c " +
               "LEFT JOIN emp.join_type_id ja " +
               "LEFT JOIN emp.modeOfHiring_id moh " +
               "LEFT JOIN emp.workingMode_id wm " +
               "LEFT JOIN emp.employee_manager_id m " +
               "LEFT JOIN emp.employee_hired hb " +
               "LEFT JOIN emp.employee_replaceby_id re " +
               "LEFT JOIN c.city ci " +
               "WHERE emp.tempPayrollId = :payrollId")
    WorkingInfoDTO findWorkingInfoByPayrollId(@Param("payrollId") String payrollId);

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
}