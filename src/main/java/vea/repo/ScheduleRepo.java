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

    @Query("SELECT s FROM Schedule s JOIN s.group g WHERE g.title = :title ORDER BY g.title, s.date, s.time")
    List<Schedule> findAllOrderByGroupAndDateAndTime(String title);

    List<Schedule> findSchedulesByClassroomAndDateAndTime(Classroom classroom, LocalDate date, LocalTime time);

    List<Schedule> findByGroup_Title(String title);

}