package vea.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vea.model.Teacher;

public interface TeacherRepo extends JpaRepository<Teacher, Long> {
}