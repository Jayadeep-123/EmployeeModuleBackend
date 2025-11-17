package com.employee.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingInfoDTO {
	
	
	  private String campus;
	    private String building;
	    private String replacementEmployee;
	    private String campusCode;
	    private String manager;
	    private String modeOfHiring;
	    private String campusType;
	    private String workingMode;
	    private String hiredBy;
	    private String location;
	    private String joiningAs;
	    private Date joiningDate;
	
	

}
