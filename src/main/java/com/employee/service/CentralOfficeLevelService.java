package com.employee.service;
 
import java.util.Optional;
import java.text.SimpleDateFormat;
import org.springframework.transaction.annotation.Transactional;
import com.employee.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import com.employee.dto.CentralOfficeChecklistDTO;
import com.employee.entity.Campus;
import com.employee.entity.City;
import com.employee.entity.Employee;
import com.employee.entity.EmployeeCheckListStatus;
import com.employee.entity.Organization;
import com.employee.entity.EmpSalaryInfo;
import com.employee.repository.CampusRepository;
import com.employee.repository.CityRepository;
import com.employee.repository.EmpAppCheckListDetlRepository; 
import com.employee.repository.EmployeeCheckListStatusRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.OrganizationRepository;
import com.employee.repository.EmpSalaryInfoRepository;
 
/**
* Service for Central Office Level operations Handles checklist updates at
* Central Office level
*/
@Service
@Transactional 
public class CentralOfficeLevelService {
 
	private static final Logger logger = LoggerFactory.getLogger(CentralOfficeLevelService.class);
 
	@Autowired
	private EmployeeRepository employeeRepository;
 
	@Autowired
	private EmpAppCheckListDetlRepository empAppCheckListDetlRepository;
 
	@Autowired
	private EmployeeCheckListStatusRepository employeeCheckListStatusRepository;
	
	@Autowired
	private CampusRepository campusrepository;
	
	@Autowired
	private CityRepository cityrepository ;
	
	@Autowired
	private OrganizationRepository organizationrepository ;
	
	@Autowired
	private EmpSalaryInfoRepository empSalaryInfoRepository;
	
	public CentralOfficeChecklistDTO updateChecklist(CentralOfficeChecklistDTO checklistDTO) {
		// Validation: Check if tempPayrollId is provided
		if (checklistDTO.getTempPayrollId() == null || checklistDTO.getTempPayrollId().trim().isEmpty()) {
			throw new ResourceNotFoundException("tempPayrollId is required. Please provide a valid temp_payroll_id.");
		}
 
		// Validation: Check if checkListIds is provided
		if (checklistDTO.getCheckListIds() == null || checklistDTO.getCheckListIds().trim().isEmpty()) {
			throw new ResourceNotFoundException(
					"checkListIds is required. Please provide checklist IDs (comma-separated string).");
		}
 
		logger.info("Updating checklist for temp_payroll_id: {}", checklistDTO.getTempPayrollId());
 
		// Step 1: Validate tempPayrollId exists in Employee table
		validateTempPayrollId(checklistDTO.getTempPayrollId());
 
		// Step 2: Find employee by temp_payroll_id
		Employee employee = employeeRepository.findByTempPayrollId(checklistDTO.getTempPayrollId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Employee not found with temp_payroll_id: " + checklistDTO.getTempPayrollId()));
 
		Integer empId = employee.getEmp_id();
		logger.info("Found employee with emp_id: {} for temp_payroll_id: {}", empId, checklistDTO.getTempPayrollId());
 
		// Step 3: Check if current status is "Pending at CO", update to "Confirm"
		if (employee.getEmp_check_list_status_id() != null
				&& "Pending at CO".equals(employee.getEmp_check_list_status_id().getCheck_app_status_name())) {
			
			Integer oldStatusId = employee.getEmp_check_list_status_id().getEmp_app_status_id();
 
			EmployeeCheckListStatus confirmStatus = employeeCheckListStatusRepository
					.findByCheck_app_status_name("Confirm").orElseThrow(() -> new ResourceNotFoundException(
							"EmployeeCheckListStatus with name 'Confirm' not found"));
			
			employee.setEmp_check_list_status_id(confirmStatus);
			logger.info("Updated employee (emp_id: {}) status from 'Pending at CO' (ID: {}) to 'Confirm' (ID: {})",
					empId, oldStatusId, confirmStatus.getEmp_app_status_id());
		} else {
			String currentStatusName = employee.getEmp_check_list_status_id() != null
					? employee.getEmp_check_list_status_id().getCheck_app_status_name()
					: "null";
			logger.info(
					"Employee (emp_id: {}) current status is '{}', not updating to 'Confirm'.",
					empId, currentStatusName);
		}
 
		// Step 4: Validate checklist IDs before saving
		validateCheckListIds(checklistDTO.getCheckListIds());
 
		// Step 5: Update emp_app_check_list_detl_id in Employee table
		employee.setEmp_app_check_list_detl_id(checklistDTO.getCheckListIds());
 
		// Step 6: Update notice_period AND Generate Permanent ID (if provided)
		if (checklistDTO.getNoticePeriod() != null && !checklistDTO.getNoticePeriod().trim().isEmpty()) {
			employee.setNotice_period(checklistDTO.getNoticePeriod().trim());
			logger.info("Updated notice period for employee (emp_id: {}): {}", empId, checklistDTO.getNoticePeriod());
 
			// Check if a permanent ID already exists. If not, generate one.
			if (employee.getPayRollId() == null || employee.getPayRollId().trim().isEmpty()) {
                
                // --- NOTE: We removed the try-catch block here to let errors show ---
                
                // This method now saves to BOTH tables
                String permanentId = generateAndSetPermanentId(employee); 
                logger.info("Successfully generated and set permanent payroll ID: {} for emp_id: {}", permanentId, empId);
                
                // 1. Set Username (it's the permanent payroll ID)
                employee.setUser_name(permanentId);
                
                // 2. Get data for password
                String firstName = employee.getFirst_name();
                java.util.Date dateOfJoin = employee.getDate_of_join(); 

                // 3. Safety checks to prevent errors
                if (firstName == null || firstName.length() < 3) {
                    logger.error("CRITICAL: Cannot generate password for emp_id: {}. First name is missing or shorter than 3 letters.", empId);
                    throw new RuntimeException("Cannot generate password. Employee first name is missing or < 3 letters.");
                }
                if (dateOfJoin == null) { 
                    logger.error("CRITICAL: Cannot generate password for emp_id: {}. Date of join is missing.", empId);
                    throw new RuntimeException("Cannot generate password. Employee date of join is missing.");
                }

                // 4. Create password (First 3 letters of first_name + date_of_join)
                String namePart = firstName.substring(0, 3);
                String finalNamePart = namePart.substring(0, 1).toUpperCase() + namePart.substring(1);

                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                String dateOfJoinString = sdf.format(dateOfJoin);
                
                String plainTextPassword = finalNamePart + dateOfJoinString;
                
                // 5. Set PLAIN TEXT Password
                employee.setPassword(plainTextPassword); 
                
                logger.info("Successfully generated default username ({}) and PLAIN TEXT password for emp_id: {}", permanentId, empId);

			} else {
				logger.warn("Employee (emp_id: {}) already has a permanent payroll ID ({}). Skipping generation.", empId, employee.getPayRollId());
			}
			
		} else {
            logger.warn("Notice period is null or empty. Permanent ID will not be generated on this update.");
        }
 
		// Step 7: Save all changes to the Employee table
		employeeRepository.save(employee); 
 
		logger.info("Successfully updated checklist for employee (emp_id: {}, temp_payroll_id: '{}')", empId,
				checklistDTO.getTempPayrollId());
 
		return checklistDTO;
	}
 
	/**
	 * Validate checklist IDs
	 */
	private void validateCheckListIds(String checkListIds) {
		if (checkListIds == null || checkListIds.trim().isEmpty()) {
			return; 
		}
 
		String[] idArray = checkListIds.split(",");
 
		for (String idStr : idArray) {
			idStr = idStr.trim(); 
 
			if (idStr.isEmpty()) {
				continue; 
			}
 
			try {
				Integer checklistId = Integer.parseInt(idStr);
 
                // Assumes the method in EmpAppCheckListDetlRepository is findByIdAndIsActive
				empAppCheckListDetlRepository.findByIdAndIsActive(checklistId, 1) 
				.orElseThrow(() -> new ResourceNotFoundException("Checklist ID " + checklistId
								+ " not found or inactive. Provided IDs: " + checkListIds));
 
				logger.debug("Validated checklist ID: {} exists and is active", checklistId);
 
			} catch (NumberFormatException e) {
				throw new ResourceNotFoundException("Invalid checklist ID format: '" + idStr
						+ "'. Provided IDs: " + checkListIds);
			}
		}
 
		logger.info("✅ All checklist IDs validated successfully: {}", checkListIds);
	}
 
	/**
	 * Validate tempPayrollId
	 */
	private void validateTempPayrollId(String tempPayrollId) {
		if (tempPayrollId == null || tempPayrollId.trim().isEmpty()) {
			throw new ResourceNotFoundException("tempPayrollId is required.");
		}
 
		employeeRepository.findByTempPayrollId(tempPayrollId.trim())
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with temp_payroll_id: " + tempPayrollId));
 
		logger.info("✅ Validated temp_payroll_id exists: {}", tempPayrollId);
	}
 
 
	/**
	 * Generates a permanent payroll ID and saves it to BOTH the Employee
	 * and the EmpSalaryInfo tables.
	 *
	 * @param employee The employee object to update (must be fully loaded).
	 * @return The generated permanent payroll ID.
	 * @throws ResourceNotFoundException if related Campus, City, Organization, or EmpSalaryInfo is not found.
	 */
	private String generateAndSetPermanentId(Employee employee) {
        logger.info("Attempting to generate permanent payroll ID for emp_id: {}", employee.getEmp_id());
        
        // --- START OF FIX ---
        // 1. Get Campus object directly from the employee
        //    We no longer call employee.getCmps_id()
        Campus campusdata = employee.getCampus_id();

        // 1a. Add a null check
        if (campusdata == null) {
            logger.error("CRITICAL: Cannot generate permanent ID for emp_id: {}. Campus ID (campus_id) is missing.", employee.getEmp_id());
            throw new ResourceNotFoundException("Cannot generate permanent ID: The Campus ID (campus_id) is missing for employee: " + employee.getEmp_id());
        }
        // --- END OF FIX ---
 
        // 2. Find City or throw
        int city_id = campusdata.getCity_id();
        City citydata = cityrepository.findById(city_id)
            .orElseThrow(() -> new ResourceNotFoundException("City not found for city_id: " + city_id));
 
        // 3. Find Organization or throw
        Integer org_id_wrapper = employee.getOrg_id(); 
 
        if (org_id_wrapper == null) {
            logger.error("CRITICAL: Cannot generate permanent ID for emp_id: {}. Organization ID (org_id) is missing.", employee.getEmp_id());
            throw new ResourceNotFoundException("Cannot generate permanent ID: The Organization ID (org_id) is missing for employee: " + employee.getEmp_id());
        }
        
        int org_id = org_id_wrapper.intValue(); 
        
        Organization org_data = organizationrepository.findById(org_id)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found for org_id: " + org_id));
 
        // --- All data is present, proceed with logic ---
        
        String payrole_city_code = citydata.getPayroll_city_code();
        long pay_role_code = org_data.getPayrollCode();
        
        // --- NEW INCREMENT LOGIC ---
        long current_max_number = org_data.getPayrollMaxNo();
        long new_max_number = current_max_number + 1; // Increment by 1
        
        logger.info("Incrementing payroll number for org_id {}: from {} to {}", 
                    org_id, current_max_number, new_max_number);
        
        // Use the NEW incremented number to build the ID
        String permanentId = payrole_city_code + pay_role_code + new_max_number;
        
        // --- SAVE TO EMPLOYEE TABLE ---
        employee.setPayRollId(permanentId); 
        logger.info("Set PayRollId on Employee entity for emp_id: {}", employee.getEmp_id());
 
 
        // --- SAVE TO EMP_SALARY_INFO TABLE ---
        logger.info("Updating EmpSalaryInfo record for emp_id: {}", employee.getEmp_id());
        
        EmpSalaryInfo salInfo = empSalaryInfoRepository.findByEmpIdAndIsActive(employee.getEmp_id(), 1)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Active EmpSalaryInfo record not found for emp_id: " + employee.getEmp_id() 
                + ". Cannot save permanent payroll ID."));
 
        salInfo.setPayrollId(permanentId);
        empSalaryInfoRepository.save(salInfo); // Save this record now
        logger.info("Successfully saved payroll ID to EmpSalaryInfo for emp_id: {}", employee.getEmp_id());
 
 
        // --- SAVE THE INCREMENTED NUMBER BACK TO THE ORGANIZATION ---
        org_data.setPayrollMaxNo(new_max_number);
        organizationrepository.save(org_data); // Save the updated counter
        logger.info("Successfully updated and saved new payrollMaxNo ({}) to Organization (org_id: {})", 
                    new_max_number, org_id);
 
        return permanentId;
    }
}