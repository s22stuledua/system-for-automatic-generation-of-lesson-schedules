package vea.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vea.model.Teacher;
import vea.service.TeacherService;

@Controller
public class TeacherUnavailabilityController {

    @Autowired
	private TeacherService teacherService;

    @GetMapping("/add-unavailability/{id}")
    public String showUnavailabilityForm(@PathVariable Long id, Model model) throws Exception {
		try {
		    model.addAttribute("teacher", teacherService.findTeacherById(id));
            return "add-unavailability";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
    }

    @PostMapping("/add-unavailability/{id}")
    public String addUnavailability(@PathVariable Long id, @RequestParam List<String> dates,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime, Model model) throws Exception {
        try {
            Teacher teacher = teacherService.findTeacherById(id);
            List<LocalDate> localDates = dates.stream().map(LocalDate::parse).collect(Collectors.toList());
            for (LocalDate date : localDates) {
                teacherService.addUnavailability(teacher, date, startTime, endTime);
            }
            return "redirect:/search-teacher/" + id;
        } catch (Exception e) {
            model.addAttribute("errormsg", e.getMessage());
            return "error-page";
        }
    }

    @GetMapping("/remove-unavailability/{teacherId}")
    public String deleteUnavailability(@PathVariable Long teacherId, @RequestParam Long unavailabilityId, Model model) {
        try {
            teacherService.deleteUnavailability(teacherId, unavailabilityId);
            return "redirect:/search-teacher/" + teacherId;
        } catch (Exception e) {
            model.addAttribute("errormsg", e.getMessage());
			return "error-page";
        }
    }
    
}