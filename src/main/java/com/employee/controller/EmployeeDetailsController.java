//
//
//package com.employee.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.employee.dto.CategoryInfoDTO1;
//import com.employee.dto.EmpQualificationDTO;
//import com.employee.dto.FamilyDetailsDTO;
//import com.employee.dto.WorkingInfoDTO;
//import com.employee.service.GetEmployeeDetailsService;
//
//@RestController
//@RequestMapping("/api/employees") // Base URL for this controller
//public class EmployeeDetailsController {
//	
//	@Autowired
//	private GetEmployeeDetailsService getEmployeeservice;
//	
//	/**
//	 * GET endpoint to retrieve working info by temp_payroll_id.
//	 * * Example URL: GET /api/employees/T1234/working-info
//	 */
//	@GetMapping("/{payrollId}/working-info")
//    public ResponseEntity<WorkingInfoDTO> getEmployeeWorkingInfoByPayrollId(
//            @PathVariable(name = "payrollId") String payrollId) { // 1. Get String from URL
//        
//        // 2. Pass the String to the service
//        WorkingInfoDTO workingInfo = getEmployeeservice.getEmployeeWorkingInfo(payrollId);
//        
//        // 3. Return the result
//        return ResponseEntity.ok(workingInfo);
//    }
//	
//	
//	@GetMapping("/{payrollId}/family-details")
//    public ResponseEntity<List<FamilyDetailsDTO>> getEmployeeFamilyDetails(
//            @PathVariable(name = "payrollId") String payrollId) {
//        
//        List<FamilyDetailsDTO> familyList = getEmployeeservice.getEmployeeFamilyDetails(payrollId);
//        return ResponseEntity.ok(familyList);
//    }
//	
//	@GetMapping("/{tempPayrollId}/qualifications")
//    public ResponseEntity<List<EmpQualificationDTO>> getQualifications(@PathVariable String tempPayrollId) {
//        
//        // 1. Call the service method, which is now simple and clean
//        List<EmpQualificationDTO> qualifications = getEmployeeservice.getQualificationsByTempPayrollId(tempPayrollId);
//        
//        // 2. Return the list. Spring Boot's Jackson library
//        //    will automatically convert this list into clean JSON.
//        return ResponseEntity.ok(qualifications);
//    }
//	
////	 @GetMapping("/category-info/{tempPayrollId}")
////	    public ResponseEntity<CategoryInfoDTO1> getCategoryInfo(@PathVariable String tempPayrollId) {
////	        
////	        // 1. Call your new service method
////	        CategoryInfoDTO1 categoryInfo = getEmployeeservice.getCategoryInfoByTempId(tempPayrollId);
////	        
////	        // 2. Return the DTO as JSON with a 200 OK status
////	        return ResponseEntity.ok(categoryInfo);
////	    }
//}
