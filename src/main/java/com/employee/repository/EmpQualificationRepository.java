//package com.employee.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import com.employee.dto.EmpQualificationDTO; 
//import com.employee.entity.EmpQualification; 
//import com.employee.entity.Employee;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface EmpQualificationRepository extends JpaRepository<EmpQualification, Integer> {
//
//    // --- DTO PROJECTION QUERY (Filtering by Temp Payroll ID) ---
//    
//    /**
//     * Retrieves qualifications and maps them to EmpQualificationDTO 
//     * using the Employee's temporary payroll ID.
//     */
//    @Query("SELECT NEW com.employee.dto.EmpQualificationDTO(" +
//            "qual.qualification_name, " +
//            "deg.degree_name, " +
//            "q.specialization, " +
//            "q.university, " +
//            "q.institute, " +
//            "q.passedout_year" +
//            ") " +
//            "FROM EmpQualification q " + 
//            "LEFT JOIN q.qualification_id qual " +
//            "LEFT JOIN q.qualification_degree_id deg " +
//            // Assuming q.emp_id is the field in EmpQualification (using tempPayrollId from Employee)
//            "WHERE q.emp_id.tempPayrollId = :payrollId AND q.is_active = 1")
//    List<EmpQualificationDTO> findQualificationsByPayrollId(@Param("payrollId") String payrollId);
//
//    // --- METHODS TO RETRIEVE RAW ENTITIES ---
//
//    /**
//     * Retrieves a list of active raw EmpQualification entities by the entire Employee object.
//     * This method fulfills the requirement for: 
//     * List<EmpQualification> qualifications = empQualificationRepository.findActiveQualificationsByEmployee(employee);
//     * * NOTE: This assumes the Employee foreign key field in EmpQualification is named 'empId' (camelCase).
//     * If the field is named 'emp_id', use findByEmp_idAndIsActive(Employee employee, int isActive);
//     */
////    List<EmpQualification> findByEmpIdAndIsActive(Employee employee, int isActive); 
//
//
//    List<EmpQualification> findByEmp_idAndIsActive(Employee employee, int isActive);
//    
//    @Query("SELECT eq FROM EmpQualification eq JOIN FETCH eq.qualification_id q WHERE eq.emp_id = :employee AND eq.is_active = 1")
//    List<EmpQualification> findActiveQualificationsByEmployee(@Param("employee") Optional<Employee> employee);
// 
//    
//    /**
//     * Retrieves a list of active raw EmpQualification entities by the Employee's permanent PayRollId.
//     * NOTE: Assumes Employee foreign key is 'empId' and Payroll ID is 'PayRollId'.
//     */
////    List<EmpQualification> findByEmpId_PayRollIdAndIsActive(String payRollId, int isActive);
//    List<EmpQualification> findByEmp_id_PayRollIdAndIsActive(String payRollId, int isActive);
//
//    /**
//     * Retrieves a list of active raw EmpQualification entities by the Employee's temporary PayRollId.
//     * NOTE: Assumes Employee foreign key is 'empId' and temp payroll ID is 'tempPayrollId'.
//     */
////    List<EmpQualification> findByEmp_id_TempPayrollIdAndIsActive(String tempPayrollId, int isActive);
//    
//    
//    @Query("SELECT eq FROM EmpQualification eq " + 
//            "WHERE eq.emp_id.tempPayrollId = :tempPayrollId AND eq.is_active = :isActive")
//     List<EmpQualification> findByEmp_id_TempPayrollIdAndIsActive(
//         @Param("tempPayrollId") String tempPayrollId, 
//         @Param("isActive") int isActive
//     );
//    
//    @Query("SELECT eq FROM EmpQualification eq " +
//            "WHERE eq.emp_id.payRollId = :payrollId AND eq.is_active = 1")
//     List<EmpQualification> findByEmployeePayrollId(@Param("payrollId") String payrollId);
// 
//
//}





package com.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.EmpQualificationDTO; 
import com.employee.entity.EmpQualification; 
import com.employee.entity.Employee;

import java.util.List;
import java.util.Optional;

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
     * CONVERTED TO @Query to avoid 'emp_id' parsing error.
     */
    // Replaces: List<EmpQualification> findByEmp_idAndIsActive(Employee employee, int isActive);
    @Query("SELECT eq FROM EmpQualification eq WHERE eq.emp_id = :employee AND eq.is_active = :isActive")
    List<EmpQualification> findByEmployeeAndActiveStatus(@Param("employee") Employee employee, @Param("isActive") int isActive);
    
    @Query("SELECT eq FROM EmpQualification eq JOIN FETCH eq.qualification_id q WHERE eq.emp_id = :employee AND eq.is_active = 1")
    List<EmpQualification> findActiveQualificationsByEmployee(@Param("employee") Optional<Employee> employee);
 
    
    /**
     * Retrieves a list of active raw EmpQualification entities by the Employee's permanent PayRollId.
     * CONVERTED TO @Query to fix the dependency error.
     */
    // Replaces: List<EmpQualification> findByEmp_id_PayRollIdAndIsActive(String payRollId, int isActive);
    @Query("SELECT eq FROM EmpQualification eq " + 
           "WHERE eq.emp_id.payRollId = :payRollId AND eq.is_active = :isActive")
    List<EmpQualification> findByEmp_id_PayRollIdAndIsActive(
        @Param("payRollId") String payRollId, 
        @Param("isActive") int isActive
    );

    /**
     * Retrieves a list of active raw EmpQualification entities by the Employee's temporary PayRollId.
     * This method was already correctly converted to @Query.
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