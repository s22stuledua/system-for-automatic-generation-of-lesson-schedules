package vea.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vea.model.Group;

public interface GroupRepo extends JpaRepository<Group, Long> {
}