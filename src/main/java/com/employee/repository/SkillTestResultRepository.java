package com.employee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.employee.dto.SkillTestResultDTO;
import com.employee.entity.SkillTestResult;

public interface SkillTestResultRepository extends JpaRepository<SkillTestResult, Integer> {

    @Query("SELECT new com.employee.dto.SkillTestResultDTO("
         + "d.tempPayrollId, "
         + "CONCAT(d.firstName, ' ', d.lastName), "
         + "s.subjectName, "
         + "r.examDate, "
         + "r.noOfQuestion, "
         + "r.noOfQuesAttempt, "
         + "r.noOfQuesUnattempt, "
         + "r.noOfQuesWrong, "
         + "r.totalMarks) "
         + "FROM SkillTestResult r "
         + "JOIN r.skillTestDetlId d "
         + "LEFT JOIN d.subjectId s "
         + "WHERE d.tempPayrollId = :tempPayrollId "
         + "AND r.isActive = 1 "
         + "ORDER BY r.examDate DESC")
    List<SkillTestResultDTO> findSkillTestDetailsByPayrollId(@Param("tempPayrollId") String tempPayrollId);
}