package vea.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vea.model.Classroom;

public interface ClassroomRepo extends JpaRepository<Classroom, Long> {

    @Query("SELECT c FROM Classroom c WHERE c.title LIKE %?1%")
	public List<Classroom> search(String keyword);
    
}