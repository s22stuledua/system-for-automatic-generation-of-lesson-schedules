package vea.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vea.model.Course;
import vea.model.Teacher;

public interface CourseRepo extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE c.title LIKE %?1%")
	public List<Course> search(String keyword);

    public List<Course> findByTeacher1OrTeacher2(Teacher teacher1, Teacher teacher2);
    
}