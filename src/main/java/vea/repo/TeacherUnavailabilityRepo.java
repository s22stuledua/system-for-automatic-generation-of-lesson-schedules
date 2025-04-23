package vea.repo;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vea.model.Teacher;
import vea.model.TeacherUnavailability;

public interface TeacherUnavailabilityRepo extends JpaRepository<TeacherUnavailability, Long> {

    List<TeacherUnavailability> findByTeacherAndDate(Teacher teacher, LocalDate date);

    List<TeacherUnavailability> findByTeacher(Teacher teacher);
    
}