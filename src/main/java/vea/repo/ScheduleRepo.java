package vea.repo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;
import vea.model.Classroom;
import vea.model.Schedule;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {

    @Transactional
    void deleteAll();

    @Query("SELECT s FROM Schedule s JOIN s.group g ORDER BY g.title, s.date, s.time")
    List<Schedule> findAllOrderByGroupAndDateAndTime();

    @Query("SELECT s FROM Schedule s JOIN s.group g WHERE g.title = :title ORDER BY s.date, s.time")
    List<Schedule> findAllOrderByGroupAndDateAndTime(String title);

    @Query("SELECT s FROM Schedule s JOIN s.classroom c WHERE c.title = :title ORDER BY s.date, s.time")
    List<Schedule> findAllOrderByClassroomAndDateAndTime(String title);

    @Query("SELECT s FROM Schedule s JOIN s.teacher t WHERE t.name = :name ORDER BY s.date, s.time")
    List<Schedule> findAllOrderByTeacherAndDateAndTime(String name);

    List<Schedule> findSchedulesByClassroomAndDateAndTime(Classroom classroom, LocalDate date, LocalTime time);

    List<Schedule> findByGroupTitle(String title);

    List<Schedule> findByGroupTitleOrderByDateAscTimeAsc(String groupTitle);

}