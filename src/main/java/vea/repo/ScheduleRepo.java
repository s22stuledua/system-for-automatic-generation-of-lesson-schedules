package vea.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.transaction.Transactional;
import vea.model.Schedule;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {

    @Transactional
    void deleteAll();

    List<Schedule> findByGroup_Title(String title);

}