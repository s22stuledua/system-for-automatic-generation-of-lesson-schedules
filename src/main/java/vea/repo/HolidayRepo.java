package vea.repo;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vea.model.Holiday;

public interface HolidayRepo extends JpaRepository<Holiday, Long> {
    
    List<Holiday> findAll();

    boolean existsByDate(LocalDate date);

}