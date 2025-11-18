package com.employee.service;

import java.time.format.DateTimeFormatter;
import java.util.List; 
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee.dto.SkillTestDetailsDto;
import com.employee.entity.Campus;
import com.employee.entity.Employee;
import com.employee.entity.EmployeeLevel;
import com.employee.entity.Gender;
import com.employee.entity.Grade;
import com.employee.entity.JoiningAs;
import com.employee.entity.Qualification;
import com.employee.entity.SkillTestDetails;
import com.employee.entity.Stream;
import com.employee.entity.Structure;
import com.employee.entity.Subject;
import com.employee.exception.ResourceNotFoundException;
import com.employee.repository.CampusRepository;
import com.employee.repository.EmployeeLevelRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.GenderRepository;
import com.employee.repository.GradeRepository;
import com.employee.repository.JoiningAsRepository;
import com.employee.repository.QualificationRepository;
import com.employee.repository.SkillTestDetailsRepository;
import com.employee.repository.StreamRepository;
import com.employee.repository.StructureRepository;
import com.employee.repository.SubjectRepository;

import jakarta.annotation.PostConstruct; 
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SkillTestDetailsService {

	@Autowired
	private SkillTestDetailsRepository skillTestDetailsRepository;
	@Autowired
	CampusRepository campusrepository;

	// Inject all repositories
	@Autowired
	private GenderRepository genderRepository;
	@Autowired
	private QualificationRepository qualificationRepository;
	@Autowired
	private JoiningAsRepository joiningAsRepository;
	@Autowired
	private StreamRepository streamRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private EmployeeLevelRepository employeeLevelRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
   @Autowired GradeRepository graderepository;
   @Autowired StructureRepository  structurerepository;
	// This map is filled by the @PostConstruct method
	private Map<String, AtomicInteger> campusCounters = new ConcurrentHashMap<>();

	// === THIS IS THE RESTART-SAFE ID LOGIC YOU MUST ADD ===
	@PostConstruct
	public void initializeCounters() {
		log.info("Initializing campus ID counters from database...");

		// 1. Get all campuses (uses new repo method)
		List<Campus> allCampuses = campusrepository.findAllWithCodeNotNull();

		for (Campus campus : allCampuses) {
			String baseKey = "TEMP" + campus.getCode(); // e.g., "TEMPX"
			int lastValue = 0;

			try {
				// 2. Find highest ID (uses new repo method)
				String lastId = skillTestDetailsRepository.findMaxTempPayrollIdByKey(baseKey + "%");

				if (lastId != null) {
					String numberPart = lastId.substring(baseKey.length());

					// 3. Safely parse the number
					if (numberPart != null && !numberPart.isEmpty()) {
						try {
							lastValue = Integer.parseInt(numberPart);
						} catch (NumberFormatException e) {
							log.warn("Could not parse number part '{}' for key {}.", numberPart, baseKey);
						}
					}
				}
			} catch (Exception e) {
				log.error("Error initializing counter for key {}: {}", baseKey, e.getMessage());
			}

			// 4. Put the last value (e.g., 5) into the in-memory map
			campusCounters.put(baseKey, new AtomicInteger(lastValue));
			log.info("Initialized counter for {}: last value = {}", baseKey, lastValue);
		}
		log.info("Campus ID counter initialization complete.");
	}
	// === END OF THE ID FIX ===

	@Transactional
	public String saveSkillTestDetails(SkillTestDetailsDto dto, int emp_id) {

		// 2. Find the Employee (No change to this repo call)
		Employee employee = employeeRepository.findById(emp_id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + emp_id));

        // --- START OF FIX ---
		// 3. Get the Campus object directly from the Employee
        //    We no longer need to call campusrepository.findById()
		Campus campus = employee.getCampus_id();

		// 4. Check that the employee is actually assigned to a campus
		if (campus == null) {
			throw new ResourceNotFoundException("Employee with id: " + emp_id + 
				" is not assigned to a campus (campus_id is null).");
		}
        // --- END OF FIX ---


		// 4. === ID Generation Logic (NOW RESTART-SAFE) ===
		String baseKey = "TEMP" + campus.getCode();

		// This map is now pre-filled with data from the database
		AtomicInteger counter = campusCounters.computeIfAbsent(baseKey, key -> new AtomicInteger(0));

		// Increments from the correct last value (e.g., 5 -> 6)
		int nextValue = counter.incrementAndGet();

		String paddedValue = String.format("%04d", nextValue);
		String generatedTempPayrollId = baseKey + paddedValue;
		log.info("Generated Temp Payroll ID: {}", generatedTempPayrollId);
		// --- End of ID logic ---

		// 5. Find all other related entities (No change to these repo calls)
		Gender gender = genderRepository.findById(dto.getGenderId())
				.orElseThrow(() -> new ResourceNotFoundException("Gender not found with id: " + dto.getGenderId()));

		Qualification qualification = qualificationRepository.findById(dto.getQualificationId()).orElseThrow(
				() -> new ResourceNotFoundException("Qualification not found with id: " + dto.getQualificationId()));

		JoiningAs joiningAs = joiningAsRepository.findById(dto.getJoiningAsId()).orElseThrow(
				() -> new ResourceNotFoundException("JoiningAs not found with id: " + dto.getJoiningAsId()));

		Stream stream = streamRepository.findById(dto.getStreamId())
				.orElseThrow(() -> new ResourceNotFoundException("Stream not found with id: " + dto.getStreamId()));

		Subject subject = subjectRepository.findById(dto.getSubjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Subject not in id: " + dto.getSubjectId()));

		EmployeeLevel employeeLevel = employeeLevelRepository.findById(dto.getEmp_level_id()).orElseThrow(
				() -> new ResourceNotFoundException("EmployeeLevel not found with id: " + dto.getEmp_level_id()));

		Grade grade = graderepository.findById(dto.getEmp_grade_id()).orElseThrow(() -> new ResourceNotFoundException("Grade not found with id: " + dto.getEmp_grade_id()));
			
			// NEW: Find Structure (Requires structureRepository)
			Structure structure = structurerepository.findById(dto.getEmp_structure_id())
			    .orElseThrow(() -> new ResourceNotFoundException("Structure not found with id: " + dto.getEmp_structure_id()));
		
		
		// 6. Create a new SkillTestDetails entity
		SkillTestDetails newDetails = new SkillTestDetails();

		// 7. Map simple fields from DTO to Entity
		newDetails.setAadhaar_no(dto.getAadhaarNo());
		newDetails.setPrevious_chaitanya_id(dto.getPreviousChaitanyaId());
		newDetails.setFirst_name(dto.getFirstName());
		newDetails.setLast_name(dto.getLastName());
		newDetails.setDob(dto.getDob());
		newDetails.setEmail(dto.getEmail());
		newDetails.setTotalExperience(dto.getTotalExperience());
		newDetails.setContact_number(dto.getContactNumber());
		newDetails.setEmpGrade(grade);
		newDetails.setEmpStructure(structure);

		// === YOUR PASSWORD LOGIC (THIS IS CORRECT) ===
		String firstName = dto.getFirstName();
		String namePart;

		if (firstName == null || firstName.length() < 3) {
			namePart = (firstName == null ? "emp" : firstName);
			log.warn("First name is null or shorter than 3 letters. Using '{}' for password part.", namePart);
		} else {
			namePart = firstName.substring(0, 3); // Get first 3 letters
		}

		java.time.LocalDate dob = dto.getDob();
		String dobPart;

		if (dob == null) {
			throw new ResourceNotFoundException("Date of Birth cannot be null for password generation.");
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
			dobPart = dob.format(formatter);
		}

		String tempPassword = namePart + dobPart;
		newDetails.setPassword(tempPassword);
		// === END OF PASSWORD LOGIC ===

		// 8. Set the GENERATED TempPayrollId
		newDetails.setTempPayrollId(generatedTempPayrollId);

		// 9. Set the related objects on the new entity
		newDetails.setGender(gender);
		newDetails.setQualification(qualification);
		newDetails.setJoiningAs(joiningAs);
		newDetails.setStream(stream);
		newDetails.setSubject(subject);
		newDetails.setEmployeeLevel(employeeLevel);
		newDetails.setEmpGrade(grade);
		newDetails.setEmpStructure(structure);
//		newDetails.setEmpGrade(g); 
//		newDetails.setEmpStructure(structure);

		// 10. Save (No change to this repo call)
		skillTestDetailsRepository.save(newDetails);
		return "Data is posted successful";
	}
}