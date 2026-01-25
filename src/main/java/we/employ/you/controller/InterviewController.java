package we.employ.you.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import we.employ.you.dao.InterviewDAO.InterviewStats;
import we.employ.you.dao.InterviewDAO.InterviewStatsCriteria;
import we.employ.you.model.Company;
import we.employ.you.model.Contact;
import we.employ.you.model.Interview;
import we.employ.you.service.InterviewService;

@RestController
public class InterviewController {

	private InterviewService interviewService;

	@Autowired
	public InterviewController(InterviewService interviewService) {
		this.interviewService = interviewService;
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getInterviews", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Interview>> getInterviews() {
		return ResponseEntity.ok(interviewService.getInterviews());
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getApplicantInterviews/{applicantId}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Interview>> getApplicantInterviews(@PathVariable String applicantId) {
		return ResponseEntity.ok(interviewService.getInterviews(Integer.parseInt(applicantId)));
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getInterview/{interviewId}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Interview> getInterview(@PathVariable String interviewId) {
		return ResponseEntity.ok(interviewService.getInterview(Integer.parseInt(interviewId)));
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getCompanies", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Company>> getCompanies() {
		return ResponseEntity.ok(interviewService.getCompanies());
	}

	@GetMapping(value = "/getContacts/{companyId}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Contact>> getContacts(@PathVariable String companyId) {
		return ResponseEntity.ok(interviewService.getContacts(Integer.parseInt(companyId)));
	}

	@CrossOrigin(origins = "*")
	@PostMapping(value = "/saveInterview", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void saveInterview(@RequestBody Interview interview) {
		interviewService.saveInterview(interview);
	}

	@CrossOrigin(origins = "*")
	@PutMapping(value = "/updateInterview", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateInterview(@RequestBody Interview interview) {
		interviewService.updateInterview(interview);
	}

	@CrossOrigin(origins = "*")
	@DeleteMapping("/deleteInterview/{interviewId}")
	public void deleteInterview(@PathVariable String interviewId) {
		interviewService.deleteInterview(Integer.parseInt(interviewId));
	}

	@CrossOrigin(origins = "*")
	@PostMapping("/getInterviewStats")
	public List<InterviewStats> getInterviewStats(@RequestBody InterviewStatsCriteria statsCriteria) {
		return interviewService.getInterviewStats(statsCriteria);
	}
}
