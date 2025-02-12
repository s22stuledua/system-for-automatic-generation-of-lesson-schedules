package vea.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vea.model.Classroom;

public interface ClassroomRepo extends JpaRepository<Classroom, Long> {
}