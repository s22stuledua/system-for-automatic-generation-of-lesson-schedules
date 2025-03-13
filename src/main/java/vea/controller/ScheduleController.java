package vea.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import vea.model.Schedule;
import vea.service.ClassroomService;
import vea.service.CourseService;
import vea.service.GroupService;
import vea.service.ScheduleService;
import vea.service.TeacherService;

@Controller
public class ScheduleController {
    
    @Autowired
    private ScheduleService scheduleService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private ClassroomService classroomService;

	@GetMapping("/schedules")
    public String getSortedSchedules(@RequestParam(required = false) String groupTitle, @RequestParam(required = false) String classroomTitle, @RequestParam(required = false) String teacherName, Model model) {
        List<Schedule> schedules;
		model.addAttribute("groups", groupService.findAllGroups());
		model.addAttribute("classrooms", classroomService.findAllClassrooms());
		model.addAttribute("teachers", teacherService.findAllTeachers());
        if (groupTitle != null && !groupTitle.isEmpty()) {
            schedules = scheduleService.getSchedulesSortedByGroupAndDateAndTime(groupTitle);
		} else if (classroomTitle != null && !classroomTitle.isEmpty()) {
            schedules = scheduleService.getSchedulesSortedByClassroomAndDateAndTime(classroomTitle);
		} else if (teacherName != null && !teacherName.isEmpty()) {
            schedules = scheduleService.getSchedulesSortedByTeacherAndDateAndTime(teacherName);
        } else {
            schedules = scheduleService.getSchedulesSortedByGroupAndDateAndTime(); 
        }
		model.addAttribute("schedules", schedules);
		model.addAttribute("rowCount", schedules.size());
        return "list-schedules";
    }

    @PostMapping("/schedule")
    public String generateSchedule(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, Model model) {
        scheduleService.generateSchedule(startDate);
		model.addAttribute("groups", groupService.findAllGroups());
		model.addAttribute("classrooms", classroomService.findAllClassrooms());
		model.addAttribute("teachers", teacherService.findAllTeachers());
        model.addAttribute("schedules", scheduleService.getSchedulesSortedByGroupAndDateAndTime());
        return "list-schedules";
    }

	@GetMapping("/add-lesson")
	public String showCreateForm(Schedule lesson, Model model) {
		try {
			model.addAttribute("group", groupService.findAllGroups());
			model.addAttribute("course", courseService.findAllCourses());
			model.addAttribute("teacher", teacherService.findAllTeachers());
			model.addAttribute("classroom", classroomService.findAllClassrooms());
			return "add-lesson";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/add-lesson")
	public String createLesson(@Valid Schedule lesson, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("group", groupService.findAllGroups());
			model.addAttribute("course", courseService.findAllCourses());
			model.addAttribute("teacher", teacherService.findAllTeachers());
			model.addAttribute("classroom", classroomService.findAllClassrooms());
			return "add-lesson";
		}
		scheduleService.createLesson(lesson);
		model.addAttribute("lesson", scheduleService.findAllSchedules());
		return "redirect:/schedules";
	}

    @GetMapping("/update-lesson/{id}")
	public String showUpdateForm(@PathVariable Long id, Model model) throws Exception {
		try {
			Schedule lesson = scheduleService.findLessonById(id);
		    model.addAttribute("lesson", lesson);
			model.addAttribute("group", groupService.findAllGroups());
			model.addAttribute("course", courseService.findAllCourses());
			model.addAttribute("teacher", teacherService.findAllTeachers());
			model.addAttribute("classroom", classroomService.findAllClassrooms());
		    return "update-lesson";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/update-lesson/{id}")
	public String updateLesson(@PathVariable Long id, @Valid Schedule lesson, BindingResult result, Model model) {
		if (result.hasErrors()) {
			lesson.setId(id);
			model.addAttribute("lesson", lesson);
			model.addAttribute("group", groupService.findAllGroups());
			model.addAttribute("course", courseService.findAllCourses());
			model.addAttribute("teacher", teacherService.findAllTeachers());
			model.addAttribute("classroom", classroomService.findAllClassrooms());
			return "update-lesson";
		}
		scheduleService.createLesson(lesson);
		model.addAttribute("lesson", scheduleService.findAllSchedules());
		return "redirect:/schedules";
	}

	@GetMapping("/remove-lesson/{id}")
	public String deleteLesson(@PathVariable Long id, Model model) throws Exception {
		try {
			scheduleService.deleteLesson(id);
		    model.addAttribute("group", groupService.findAllGroups());
		    return "redirect:/schedules";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

}