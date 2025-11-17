//package com.employee.service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.employee.dto.CategoryInfoDTO1;
//import com.employee.dto.EmpQualificationDTO;
//import com.employee.dto.FamilyDetailsDTO;
//import com.employee.dto.WorkingInfoDTO;
//import com.employee.exception.ResourceNotFoundException;
//import com.employee.repository.EmpFamilyDetailsRepository;
//import com.employee.repository.EmpQualificationRepository;
//import com.employee.repository.EmployeeRepository;
//
//@Service
//public class GetEmployeeDetailsService {
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//    
//    @Autowired
//    private EmpFamilyDetailsRepository familyDetailsRepository;
//    
//    @Autowired
//    private EmpQualificationRepository qualificationRepository;
//    
//    /**
//     * Retrieves the consolidated working information for a single employee
//     * by their temp_payroll_id.
//     *
//     * @param payrollId The temp_payroll_id of the employee.
//     * @return The populated WorkingInfoDTO.
//     * @throws ResourceNotFoundException if no employee is found.
//     */
//    public WorkingInfoDTO getEmployeeWorkingInfo(String payrollId) { 
//        
//        // 1. Call the new, correct repository method
//        WorkingInfoDTO workingInfo = employeeRepository.findWorkingInfoByPayrollId(payrollId);
//        
//        // 2. Check for null
//        if (workingInfo == null) {
//            throw new ResourceNotFoundException("Working information not found for payroll ID: " + payrollId);
//        }
//        
//        // 3. Return the DTO
//        return workingInfo;
//    }
//    
//    
//public List<FamilyDetailsDTO> getEmployeeFamilyDetails(String payrollId) {
//        
//        // Call the new repository method
//        List<FamilyDetailsDTO> familyList = familyDetailsRepository.findFamilyDetailsByPayrollId(payrollId);
//        
//        // This is important: An empty list is NOT an error.
//        // It just means the employee has no family members added.
//        // So, we return the list as-is (it might be empty).
//        if (familyList == null) {
//            return new ArrayList<>(); // Return an empty list instead of null
//        }
//        
//        return familyList;
//    }
//
//
//public List<EmpQualificationDTO> getQualificationsByTempPayrollId(String tempId) {
//    
//    // 1. Call the new repository method that does all the work.
//    List<EmpQualificationDTO> qualificationList = qualificationRepository.findQualificationsByPayrollId(tempId);
//
//    // 2. Good Practice (as you did in your FamilyDetails example):
//    //    Ensure we always return a list, never null.
//    if (qualificationList == null) {
//        return new ArrayList<>();
//    }
//    
//    // 3. Return the DTO list (it might be empty, which is correct).
//    return qualificationList;
//}
//
////public CategoryInfoDTO1 getCategoryInfoByTempId(String tempId) {
////    
////    // 1. Call your new DTO query method.
////    CategoryInfoDTO1 categoryInfo = employeeRepository.findCategoryInfoByPayrollId(tempId);
////
////    // 2. This is an important check:
////    //    If the query returns null, it means no employee was found.
////    if (categoryInfo == null) {
////        // This exception will be automatically turned into a 404/500 error.
////        throw new RuntimeException("No employee category info found for payroll ID: " + tempId);
////    }
////
////    // 3. Return the DTO
////    return categoryInfo;
////}
//}