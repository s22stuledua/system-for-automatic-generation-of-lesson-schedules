package vea.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vea.model.Course;
import vea.model.Group;
import vea.model.Semester;
import vea.repo.CourseRepo;
import vea.repo.GroupRepo;
import vea.service.GroupService;

@Service
public class GroupServiceImpl implements GroupService {
    
    @Autowired
	private GroupRepo groupRepo;

	@Autowired
	private CourseRepo courseRepo;

	@Override
	public List<Group> findAllGroups() {
		return groupRepo.findAll();
	}

	@Override
	public List<Group> searchGroupByTitle(String keyword) {
		if (keyword != null) {
			return groupRepo.search(keyword);
		}
		return groupRepo.findAll();
	}

	@Override
	public List<Group> getGroupsByCourseId(Long courseId) {
		Course course = courseRepo.findById(courseId).orElseThrow(() -> new RuntimeException("Kurss nav atrasts"));
        return groupRepo.findByCourse(course);
    }

	@Override
	public Group findGroupById(Long id) throws Exception {
		return groupRepo.findById(id).orElseThrow(() -> new Exception("Grupa ar šo ID nav atrasta"));
	}

	@Override
	public List<Group> getGroupsSortedBySemester(Semester semester) {
		return groupRepo.findBySemester(semester);
	}

	@Override
	public void createGroup(Group group) {
		groupRepo.save(group);
	}

	@Override
	public void deleteGroup(Long id) throws Exception {
		Group group = groupRepo.findById(id).orElseThrow(() -> new Exception("Grupa ar šo ID nav atrasta"));
		groupRepo.deleteById(group.getId());
	}

}