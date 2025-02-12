package vea.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vea.model.Course;

public interface CourseRepo extends JpaRepository<Course, Long> {
}