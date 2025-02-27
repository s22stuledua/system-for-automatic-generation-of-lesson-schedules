package vea.service;

import java.util.List;
import vea.model.Group;

public interface GroupService {
    
    public List<Group> findAllGroups();

	public List<Group> searchGroupByTitle(String keyword);

	public List<Group> getGroupsByCourseId(Long courseId); 

	public Group findGroupById(Long id) throws Exception;

	public void createGroup(Group group);

	public void deleteGroup(Long id) throws Exception;

}