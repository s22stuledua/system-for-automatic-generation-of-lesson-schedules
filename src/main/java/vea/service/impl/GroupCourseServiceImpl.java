package vea.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vea.model.GroupCourse;
import vea.repo.GroupCourseRepo;
import vea.service.GroupCourseService;

@Service
public class GroupCourseServiceImpl implements GroupCourseService {

    @Autowired
	private GroupCourseRepo groupCourseRepo;

	@Override
	public List<GroupCourse> findAllGroupCourses() {
		return groupCourseRepo.findAll();
	}

	@Override
	public GroupCourse findGroupCourseById(Long id) throws Exception {
		return groupCourseRepo.findById(id).orElseThrow(() -> new Exception("Kopējais kurss ar šo ID netika atrasts"));
	}

	@Override
	public void createGroupCourse(GroupCourse groupCourse) {
		groupCourseRepo.save(groupCourse);
	}

	@Override
	public void deleteGroupCourse(Long id) throws Exception {
		GroupCourse groupCourse = groupCourseRepo.findById(id).orElseThrow(() -> new Exception("Kopējais kurss ar šo ID netika atrasts"));
		groupCourseRepo.deleteById(groupCourse.getId());
	}
    
}