package vea.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vea.model.Course;
import vea.model.Teacher;
import vea.repo.CourseRepo;
import vea.repo.TeacherRepo;
import vea.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {
    
    @Autowired
	private CourseRepo courseRepo;

	@Autowired
	private TeacherRepo teacherRepo;

	@Override
	public List<Course> findAllCourses() {
		return courseRepo.findAll();
	}

	@Override
	public List<Course> searchCourseByTitle(String keyword) {
		if (keyword != null) {
			return courseRepo.search(keyword);
		}
		return courseRepo.findAll();
	}

	@Override
	public List<Course> getSortedCourses(boolean ascending) {
		if (ascending) {
            return courseRepo.findAll(Sort.by(Sort.Direction.ASC, "title"));
        } else {
            return courseRepo.findAll();
        }
    }

	@Override
	public List<Course> getCoursesByTeacherId(Long teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId).orElseThrow(() -> new RuntimeException("Pasniedzējs ar šo ID netika atrasts"));
        return courseRepo.findByTeacher1OrTeacher2(teacher, teacher);
    }

	@Override
	public Course findCourseById(Long id) throws Exception {
		return courseRepo.findById(id).orElseThrow(() -> new Exception("Kurss ar šo ID netika atrasts"));
	}

	@Override
	public void createCourse(Course course) {
		courseRepo.save(course);
	}

	@Override
	public void deleteCourse(Long id) throws Exception {
		Course course = courseRepo.findById(id).orElseThrow(() -> new Exception("Kurss ar šo ID netika atrasts"));
		courseRepo.deleteById(course.getId());
	}

}