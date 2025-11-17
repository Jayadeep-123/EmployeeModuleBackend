package com.employee.controller;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.CentralOfficeChecklistDTO;
import com.employee.exception.ResourceNotFoundException;
import com.employee.service.CentralOfficeLevelService;
 
/**
 * Controller to handle API requests for Central Office operations.
 */
@RestController
@RequestMapping("/api/co")
// IMPORTANT: Adjust this URL to match your React app's URL
@CrossOrigin(origins = "http://localhost:3000") 
public class CentralOfficeController {
 
    private static final Logger logger = LoggerFactory.getLogger(CentralOfficeController.class);
 
    @Autowired
    private CentralOfficeLevelService centralOfficeLevelService;
 
    /**
     * Updates the employee checklist, sets notice period, and generates
     * a permanent payroll ID if the notice period is set.
     *
     * This endpoint handles partial updates, so we use @PatchMapping.
     *
     * @param checklistDTO The DTO containing tempPayrollId, checkListIds, and noticePeriod.
     * @return A ResponseEntity containing the updated DTO on success, or an error message on failure.
     */
    @PostMapping("/update-checklist")
    public ResponseEntity<?> updateChecklist(@RequestBody CentralOfficeChecklistDTO checklistDTO) {
        
        logger.info("Received request to update checklist for temp_payroll_id: {}", checklistDTO.getTempPayrollId());
 
        try {
            // Call the service method
            CentralOfficeChecklistDTO updatedDto = centralOfficeLevelService.updateChecklist(checklistDTO);
            
            // On success, return 200 OK with the updated DTO
            return ResponseEntity.ok(updatedDto);
 
        } catch (ResourceNotFoundException e) {
            // If the service throws a ResourceNotFoundException, return 404
            logger.warn("Update failed for temp_payroll_id: {}. Reason: {}", 
                         checklistDTO.getTempPayrollId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
 
        } catch (Exception e) {
            // For any other unexpected error, return 500
            logger.error("An unexpected error occurred while updating checklist for temp_payroll_id: {}", 
                          checklistDTO.getTempPayrollId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}