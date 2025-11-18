package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.EmpQualificationDTO;
import com.employee.dto.SimilarInstituteEmployeeDTO;
import com.employee.entity.EmpQualification;
import com.employee.entity.Employee;

@Repository
public interface EmpQualificationRepository extends JpaRepository<EmpQualification, Integer> {

    // --- DTO PROJECTION QUERY (Filtering by Temp Payroll ID) ---
    
    /**
     * Retrieves qualifications and maps them to EmpQualificationDTO 
     * using the Employee's temporary payroll ID.
     */
    @Query("SELECT NEW com.employee.dto.EmpQualificationDTO(" +
            "qual.qualification_name, " +
            "deg.degree_name, " +
            "q.specialization, " +
            "q.university, " +
            "q.institute, " +
            "q.passedout_year" +
            ") " +
            "FROM EmpQualification q " + 
            "LEFT JOIN q.qualification_id qual " +
            "LEFT JOIN q.qualification_degree_id deg " +
            "WHERE q.emp_id.tempPayrollId = :payrollId AND q.is_active = 1")
    List<EmpQualificationDTO> findQualificationsByPayrollId(@Param("payrollId") String payrollId);

    // --- METHODS TO RETRIEVE RAW ENTITIES ---

    /**
     * Retrieves a list of active raw EmpQualification entities by the entire Employee object.
     */
    @Query("SELECT eq FROM EmpQualification eq WHERE eq.emp_id = :employee AND eq.is_active = :isActive")
    List<EmpQualification> findByEmployeeAndActiveStatus(@Param("employee") Employee employee, @Param("isActive") int isActive);
    
    @Query("SELECT eq FROM EmpQualification eq JOIN FETCH eq.qualification_id q WHERE eq.emp_id = :employee AND eq.is_active = 1")
    List<EmpQualification> findActiveQualificationsByEmployee(@Param("employee") Optional<Employee> employee);
 
    
    /**
     * Retrieves a list of active raw EmpQualification entities by the Employee's permanent PayRollId.
     */
    @Query("SELECT eq FROM EmpQualification eq " + 
           "WHERE eq.emp_id.payRollId = :payRollId AND eq.is_active = :isActive")
    List<EmpQualification> findByEmp_id_PayRollIdAndIsActive(
        @Param("payRollId") String payRollId, 
        @Param("isActive") int isActive
    );

    /**
     * Retrieves a list of active raw EmpQualification entities by the Employee's temporary PayRollId.
     */
    @Query("SELECT eq FROM EmpQualification eq " + 
            "WHERE eq.emp_id.tempPayrollId = :tempPayrollId AND eq.is_active = :isActive")
     List<EmpQualification> findByEmp_id_TempPayrollIdAndIsActive(
         @Param("tempPayrollId") String tempPayrollId, 
         @Param("isActive") int isActive
     );
    
    @Query("SELECT eq FROM EmpQualification eq " +
            "WHERE eq.emp_id.payRollId = :payrollId AND eq.is_active = 1")
     List<EmpQualification> findByEmployeePayrollId(@Param("payrollId") String payrollId);

   

}