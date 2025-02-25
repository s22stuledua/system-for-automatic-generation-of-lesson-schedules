package vea.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;
import vea.model.Schedule;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s JOIN s.group g ORDER BY g.title, s.time")
    List<Schedule> findAllOrderByGroupAndTime();

    @Transactional
    void deleteAll();

    List<Schedule> findAllByOrderByGroupTitle();

    List<Schedule> findByGroup_Title(String title);

}