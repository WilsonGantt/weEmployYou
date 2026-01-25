package we.employ.you.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import we.employ.you.dao.InterviewDAO;
import we.employ.you.dao.InterviewDAO.InterviewStats;
import we.employ.you.dao.InterviewDAO.InterviewStatsCriteria;
import we.employ.you.model.Company;
import we.employ.you.model.Contact;
import we.employ.you.model.Interview;

@Service
public class InterviewServiceImpl implements InterviewService {

	private final InterviewDAO interviewDAO;

	@Autowired
	public InterviewServiceImpl(InterviewDAO interviewDAO) {
		this.interviewDAO = interviewDAO;
	}

	@Override
	@Transactional
	public List<Interview> getInterviews() {
		return interviewDAO.getInterviews();
	}

	@Override
	@Transactional
	public List<Interview> getInterviews(int applicantId) {
		return interviewDAO.getInterviews().stream().filter(interview -> interview.getApplicant().getApplicantId() == applicantId).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Interview getInterview(int interviewId) {
		return interviewDAO.getInterview(interviewId);
	}

	@Override
	@Transactional
	public List<Company> getCompanies() {
		return interviewDAO.getCompanies();
	}

	@Override
	@Transactional
    public List<Contact> getContacts(int companyId) {
    	return interviewDAO.getContacts(companyId);
    }

	@Override
	@Transactional
	public void saveInterview(Interview interview) {
		interviewDAO.saveInterview(interview);
	}

	@Override
	@Transactional
	public void updateInterview(Interview interview) {
		interviewDAO.updateInterview(interview);
	}

	@Override
	@Transactional
	public void deleteInterview(int interviewId) {
		interviewDAO.deleteInterview(interviewId);
	}

	@Override
	@Transactional
	public List<InterviewStats> getInterviewStats(InterviewStatsCriteria statsCriteria) {
		return interviewDAO.getInterviewStats(statsCriteria);
	}
}
