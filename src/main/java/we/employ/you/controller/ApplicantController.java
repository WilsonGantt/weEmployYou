package we.employ.you.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import we.employ.you.exception.MissingDataException;
import we.employ.you.exception.ValidationException;
import we.employ.you.model.Applicant;
import we.employ.you.model.ApplicantFile;
import we.employ.you.model.ErrorResponse;
import we.employ.you.model.Response;
import we.employ.you.model.User;
import we.employ.you.service.ApplicantService;

@RestController
public class ApplicantController {

	private final ApplicantService applicantService;

	@Autowired
	public ApplicantController(ApplicantService applicantService) {
		this.applicantService = applicantService;
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getApplicants", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Applicant>> getApplicants() {
		return ResponseEntity.ok(applicantService.getApplicants());
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getApplicant/{applicantId}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Applicant> getApplicant(@PathVariable("applicantId") String applicantId) {
		return ResponseEntity.ok(applicantService.getApplicant(Integer.parseInt(applicantId)));
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getApplicantPhoto/{applicantId}")
	@ResponseBody
	public byte[] getApplicantPhoto(@PathVariable("applicantId") String applicantId) {
		//byte[] photo = applicantService.getApplicantPhoto(Integer.parseInt(applicantId));
		return applicantService.getApplicantPhoto(Integer.parseInt(applicantId));
	}

	@CrossOrigin(origins = "*")
	@PostMapping(value = "/saveApplicantPhoto")
	public void saveApplicantPhoto(@RequestParam("applicantId") String applicantId,
			@RequestParam("applicantPhoto") MultipartFile photo) throws IOException {
		applicantService.saveApplicantPhoto(Integer.parseInt(applicantId), photo.getBytes());
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getRecruiters", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> getRecruiters() {
		return ResponseEntity.ok(applicantService.getRecruiters());
	}

	@CrossOrigin(origins = "*")
	@PostMapping(value = "/saveApplicant", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Applicant> saveApplicant(@RequestBody Applicant applicant) {
		applicant.setApplicantId(applicantService.saveApplicant(applicant));
		return ResponseEntity.ok(applicant);
	}

	@CrossOrigin(origins = "*")
	@PutMapping(value = "/updateApplicant", consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends Response> updateApplicant(@RequestBody Applicant applicant) {
		Response response;

		try {
			applicantService.updateApplicant(applicant);

			response = applicant;
		} catch (ValidationException exception) {
			response = new ErrorResponse();
			response.setResponseMessage(exception.getMessage());
		}

		return ResponseEntity.ok(response);
	}

	@CrossOrigin(origins = "*")
	@DeleteMapping(value = "/deleteApplicant/{applicantId}")
	public void deleteApplicant(@PathVariable String applicantId) throws MissingDataException {
		applicantService.deleteApplicant(Integer.parseInt(applicantId));
	}

	@CrossOrigin(origins = "*")
	@PostMapping(value = "/saveApplicantFile", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ApplicantFile>> saveApplicantFile(@RequestParam("fileName") String fileName,
			@RequestParam("applicantId") String applicantId,
			@RequestParam("isResume") String isResume,
			@RequestParam("applicantFile") MultipartFile file) throws IOException {

		ApplicantFile applicantFile = new ApplicantFile();
		applicantFile.setFileName(fileName);
		applicantFile.setApplicant(new Applicant(Integer.parseInt(applicantId)));
		applicantFile.setResumeIndicator(Boolean.parseBoolean(isResume));
		applicantFile.setFile(file.getBytes());

		applicantService.saveApplicantFile(applicantFile);

		return ResponseEntity.ok(applicantService.getApplicantFiles(Integer.parseInt(applicantId)));
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/downloadApplicantFile/{fileId}")
	@ResponseBody
	public byte[] downloadApplicantFile(@PathVariable("fileId") String fileId) throws IOException, MissingDataException {
		return applicantService.downloadApplicantFile(Integer.parseInt(fileId));
	}

	@CrossOrigin(origins = "*")
	@DeleteMapping(value = "/deleteApplicantFile/{fileId}")
	public void deleteApplicantFile(@PathVariable("fileId") String fileId) {
		applicantService.deleteApplicantFile(Integer.parseInt(fileId));
	}
}
