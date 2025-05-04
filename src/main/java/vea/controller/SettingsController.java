package vea.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vea.model.Settings;
import vea.service.SettingsService;

@Controller
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping("/settings")
    public String showConfig(Model model) {
        Settings settings = settingsService.getSettings();
        model.addAttribute("settings", settings);
        return "settings-page";
    }

    @PostMapping("/settings")
    public String updateConfig(@ModelAttribute Settings settings) {
        settingsService.updateSettings(settings);
        return "redirect:/settings";
    }
    
}