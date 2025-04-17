package vea.service;

import java.util.List;
import vea.model.Group;
import vea.model.Semester;

public interface GroupService {
    
    public List<Group> findAllGroups();

	public List<Group> searchGroupByTitle(String keyword);

	public List<Group> getGroupsByCourseId(Long courseId); 

	public Group findGroupById(Long id) throws Exception;

	public List<Group> getGroupsSortedBySemester(Semester semester);

	public int calculateTotalLessonsBySemester(Semester semester);

	public void createGroup(Group group);

	public void deleteGroup(Long id) throws Exception;

}