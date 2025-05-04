package vea.service;

import vea.model.Settings;

public interface SettingsService {

    public Settings getSettings();
    
    public void updateSettings(Settings settings);
    
}