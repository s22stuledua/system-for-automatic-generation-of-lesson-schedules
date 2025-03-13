package vea.service;

import java.util.List;
import vea.model.Course;

public interface CourseService {

	public List<Course> findAllCourses();

	public List<Course> searchCourseByTitle(String keyword);

	public List<Course> getSortedCourses(boolean ascending);

	public List<Course> getCoursesByTeacherId(Long teacherId); 

	public Course findCourseById(Long id) throws Exception;

	public Integer getTotalLessonsByTeacherId(Long teacherId);

	public void createCourse(Course course);

	public void deleteCourse(Long id) throws Exception;

}