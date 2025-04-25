package vea.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vea.model.Course;
import vea.model.Group;
import vea.model.Semester;

public interface GroupRepo extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g WHERE g.title LIKE %?1%")
	public List<Group> search(String keyword);

    @Query("SELECT g FROM Group g WHERE g.course1 = :course OR g.course2 = :course OR g.course3 = :course "
    + "OR g.course4 = :course OR g.course5 = :course OR g.course6 = :course OR g.course7 = :course "
    + "OR g.course8 = :course OR g.course9 = :course OR g.course10 = :course OR g.course11 = :course")
    List<Group> findByCourse(@Param("course") Course course);

    @Query("SELECT g FROM Group g WHERE g.semester = :semester")
    List<Group> findBySemester(Semester semester);
    
}