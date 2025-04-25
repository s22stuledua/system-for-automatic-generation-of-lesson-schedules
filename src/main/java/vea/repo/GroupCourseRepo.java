package vea.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vea.model.GroupCourse;

public interface GroupCourseRepo extends JpaRepository<GroupCourse, Long> {
}