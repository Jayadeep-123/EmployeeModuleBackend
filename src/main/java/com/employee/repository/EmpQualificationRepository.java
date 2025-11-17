// /repository/EmpQualificationRepository.java
package com.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.EmpQualificationDTO; // Use your DTO
import com.employee.entity.EmpQualification; // Use your entity
import java.util.List;

@Repository
public interface EmpQualificationRepository extends JpaRepository<EmpQualification, Integer> {

    /**
     * This query follows your "Family Details" pattern.
     * It builds the EmpQualificationDTO directly, joining all necessary tables
     * and filtering by the employee's temporary payroll ID.
     */
    @Query("SELECT NEW com.employee.dto.EmpQualificationDTO(" +
           
           // 1. Get 'qualification_name' from the joined Qualification entity
           "qual.qualification_name, " +
           
           // 2. Get 'degree_name' from the joined QualificationDegree entity
           "deg.degree_name, " +
           
           // 3. Get fields directly from EmpQualification
           "q.specialization, " +
           "q.university, " +
           "q.institute, " +
           "q.passedout_year" +
           
           ") " +
           "FROM EmpQualification q " + // 'q' is the alias for EmpQualification
           
           // Join with the related entities
           "LEFT JOIN q.qualification_id qual " +
           "LEFT JOIN q.qualification_degree_id deg " +
           
           
           // Filter by the tempPayrollId from the joined Employee entity
           // and ensure the record is active
           "WHERE q.emp_id.tempPayrollId = :payrollId AND q.is_active = 1")

    List<EmpQualificationDTO> findQualificationsByPayrollId(@Param("payrollId") String payrollId);
}