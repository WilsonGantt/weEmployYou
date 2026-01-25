package we.employ.you.service;

import java.util.List;

import we.employ.you.dao.InterviewDAO.InterviewStats;
import we.employ.you.dao.InterviewDAO.InterviewStatsCriteria;
import we.employ.you.model.Company;
import we.employ.you.model.Contact;
import we.employ.you.model.Interview;

public interface InterviewService {

	public List<Interview> getInterviews();

    public List<Interview> getInterviews(int applicantId);
    
    public Interview getInterview(int interviewId);
    
    public List<Company> getCompanies();
    
    public List<Contact> getContacts(int companyId);
    
    public void saveInterview(Interview interview);
    
    public void updateInterview(Interview interview);
    
    public void deleteInterview(int interviewId);
    
    public List<InterviewStats> getInterviewStats(InterviewStatsCriteria statsCriteria);
}
