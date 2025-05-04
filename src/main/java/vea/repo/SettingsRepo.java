package vea.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vea.model.Settings;

public interface SettingsRepo extends JpaRepository<Settings, Long> {
}