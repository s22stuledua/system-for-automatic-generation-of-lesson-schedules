package vea.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vea.model.Teacher;

public interface TeacherRepo extends JpaRepository<Teacher, Long> {
    
    @Query("SELECT t FROM Teacher t WHERE t.name LIKE %?1%")
	public List<Teacher> search(String keyword);

}