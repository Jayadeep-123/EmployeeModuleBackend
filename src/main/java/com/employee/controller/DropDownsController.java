package com.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.CampusContactDTO;
import com.employee.dto.CampusDto;
import com.employee.dto.GenericDropdownDTO;
import com.employee.dto.OrganizationDTO;
import com.employee.entity.CampusContact;
import com.employee.service.DropDownService;

@RestController
@RequestMapping("/api/employeeModule")
public class DropDownsController {
	
	@Autowired DropDownService empDropdownService;
	
	
	 @GetMapping("/marital-status")
	    public List<GenericDropdownDTO> getMaritalStatusTypes() {
	        return empDropdownService.getMaritalStatusTypes();
	    }
	 @GetMapping("/qualifications")
	    public List<GenericDropdownDTO> getQualificationTypes() {
	        return empDropdownService.getQualificationTypes();
	    }
	 
	 @GetMapping("/work-mode")
	    public List<GenericDropdownDTO> getWorkModeTypes() {
	        return empDropdownService.getWorkModeTypes();
	    }
	 
	 @GetMapping("/joining-as")
	    public List<GenericDropdownDTO> getJoinAsTypes() {
	        return empDropdownService.getJoinAsTypes();
	    }
	 @GetMapping("/mode-of-hiring")
	    public List<GenericDropdownDTO> getModeOfHiringTypes() {
	        return empDropdownService.getModeOfHiringTypes();
	    }
	 
	 @GetMapping("/employee-type")
	    public List<GenericDropdownDTO> getEmployeeTypes() {
	        return empDropdownService.getEmployeeTypes();
	    }
	 @GetMapping("/countries")
	    public List<GenericDropdownDTO> getCountries() {
	        return empDropdownService.getCountries();
	    }
	 @GetMapping("/employee-payment-type")
	    public List<GenericDropdownDTO> getEmployeePaymentTypes() {
	        return empDropdownService.getEmployeePaymentTypes();
	    }
	 @GetMapping("/department/{empTypeId}")
	    public List<GenericDropdownDTO> getDepartments(@PathVariable int empTypeId) {
	        return empDropdownService.getActiveDepartmentsByEmpTypeId(empTypeId);
	    }
	 @GetMapping("/designation/{departmentId}")
	    public List<GenericDropdownDTO> getDesignations(@PathVariable int departmentId) {
	        return empDropdownService.getDesignations(departmentId);
	    }
	 @GetMapping("/degree/{qualificationId}")
	    public List<GenericDropdownDTO> getDegrees(@PathVariable int qualificationId ) {
	        return empDropdownService.getDegrees(qualificationId);
	    }
	 @GetMapping("/subjects")
	 public List<GenericDropdownDTO> getSubjects() {
	     return empDropdownService.getSubjects();
	 }
	 @GetMapping("/campusDetl/{campusId}")
	    public CampusDto getCampusById(@PathVariable int campusId) {
	        return empDropdownService.getActiveCampusById(campusId);
	    }
	 
	 @GetMapping("/grade")
	 public List<GenericDropdownDTO> getGrades() {
	     return empDropdownService.getActiveGrades();
	 }
	 
	  @GetMapping("/structures")
	    public List<GenericDropdownDTO> getActiveStructures() {
	        return empDropdownService.getActiveStructures();
	    }
	  @GetMapping("/costcenters")
	    public List<GenericDropdownDTO> getActiveCostCenters() {
	        return empDropdownService.getCostCenters();
	    }
	  @GetMapping("/organizations/active")
	    public List<OrganizationDTO> getActiveOrganizations() {
	        return empDropdownService.getAllActiveOrganizations();
	    }
	  
	 
	  @GetMapping("/{campusId}/organizations")
	    public List<GenericDropdownDTO> getOrganizations(@PathVariable int campusId) {
	        return empDropdownService.getOrganizationsByCampusId(campusId);
	    }
	  
	  @GetMapping("/{campusId}/building")
	    public List<GenericDropdownDTO> getBuildingsByCampusId(@PathVariable int campusId) {
	        return empDropdownService.getBuildingsByCampusId(campusId);
	    }
	    @GetMapping("/streams")
	    public List<GenericDropdownDTO> getAllStreams() {
	        return empDropdownService.getAllActiveStreams();
	    }
	    
	    @GetMapping("/employeeLevels")
	    public List<GenericDropdownDTO> getEmployeeLevels() {
	        return empDropdownService.getAllActiveEmpLevels();
	    }
	    
	    @GetMapping("/active/ORhanizationBanks")
	    public List<GenericDropdownDTO> getAllActiveBanks() {
	        return empDropdownService.getAllActiveBanks();
	    }
	    @GetMapping("/employees/active")
	    public List<GenericDropdownDTO> getActiveEmployees() {
	        return empDropdownService.getActiveEmployees();
	    }

	    @GetMapping("/employees/inactive")
	    public List<GenericDropdownDTO> getInactiveEmployees() {
	        return empDropdownService.getInactiveEmployees();
	    }
	    
	    @GetMapping("/campsEmployess/{campusId}")
	    public List<CampusContactDTO>  getContactsByCampus(@PathVariable Integer campusId) {
	        return empDropdownService.getActiveContactsByCampusId(campusId);
	    }
}