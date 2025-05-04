package vea.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vea.model.Settings;
import vea.repo.SettingsRepo;
import vea.service.SettingsService;

@Service
public class SettingsServiceImlp implements SettingsService {

    @Autowired
    private SettingsRepo settingsRepo;

    @Override
    public Settings getSettings() {
        return settingsRepo.findById(1L).orElseGet(Settings::new);
    }
    
    @Override
    public void updateSettings(Settings settings) {
        if (!settingsRepo.existsById(1L)) {
            settings.setId(null);
        } else {
            settings.setId(1L);
        }
        settingsRepo.save(settings);
    }
    
}