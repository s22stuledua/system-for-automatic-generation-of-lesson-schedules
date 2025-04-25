package vea.service;

import java.util.List;
import vea.model.GroupCourse;

public interface GroupCourseService {

    public List<GroupCourse> findAllGroupCourses();

	public GroupCourse findGroupCourseById(Long id) throws Exception;

	public void createGroupCourse(GroupCourse groupCourse);

	public void deleteGroupCourse(Long id) throws Exception;
    
}