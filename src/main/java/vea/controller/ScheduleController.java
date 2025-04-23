package vea.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
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
import vea.model.Group;
import vea.model.Schedule;
import vea.model.Semester;
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

	private final List<LocalTime> startTimes = Arrays.asList(
        LocalTime.of(9, 0),
        LocalTime.of(10, 40),
        LocalTime.of(13, 0),
        LocalTime.of(14, 40),
        LocalTime.of(16, 15),
        LocalTime.of(17, 50),
        LocalTime.of(19, 25)
    );

	private final List<LocalTime> endTimes = Arrays.asList(
        LocalTime.of(10, 30),
        LocalTime.of(12, 10),
        LocalTime.of(14, 30),
        LocalTime.of(16, 10),
        LocalTime.of(17, 45),
        LocalTime.of(19, 20),
        LocalTime.of(20, 55)
    );

	@GetMapping("/schedules")
    public String getSortedSchedules(@RequestParam(required = false) String groupTitle, 
	@RequestParam(required = false) String classroomTitle, @RequestParam(required = false) String teacherName, Model model) {
        List<Schedule> schedules;
		int totalLessons = 0;
		model.addAttribute("groups", groupService.findAllGroups());
		model.addAttribute("classrooms", classroomService.findAllClassrooms());
		model.addAttribute("teachers", teacherService.findAllTeachers());
        if (groupTitle != null && !groupTitle.isEmpty()) {
            schedules = scheduleService.getSchedulesSortedByGroupAndDateAndTime(groupTitle);
			totalLessons = scheduleService.calculateGroupLessons(groupTitle);	
		} else if (classroomTitle != null && !classroomTitle.isEmpty()) {
            schedules = scheduleService.getSchedulesSortedByClassroomAndDateAndTime(classroomTitle);
		} else if (teacherName != null && !teacherName.isEmpty()) {
            schedules = scheduleService.getSchedulesSortedByTeacherAndDateAndTime(teacherName);
        } else {
            schedules = scheduleService.getSchedulesSortedByGroupAndDateAndTime(); 
			if (!schedules.isEmpty()) {
				Schedule firstSchedule = schedules.get(0);
				Group firstGroup = firstSchedule.getGroup();
				Semester semester = firstGroup.getSemester();
				totalLessons = groupService.calculateTotalLessonsBySemester(semester);
			}
        }
		model.addAttribute("schedules", schedules);
		model.addAttribute("rowCount", schedules.size());
		if (totalLessons == 0) {
			model.addAttribute("totalLessons", "Nav definēts");
		} else {
			model.addAttribute("totalLessons", totalLessons);
		}
        return "list-schedules";
    }

    @PostMapping("/generate-schedule")
    public String generateSchedule(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, 
	@RequestParam Semester selectedSemester, Model model) {
        scheduleService.generateSchedule(startDate, selectedSemester);
		List<Schedule> schedules = scheduleService.getSchedulesSortedByGroupAndDateAndTime();
		int totalLessons = groupService.calculateTotalLessonsBySemester(selectedSemester);
		model.addAttribute("totalLessons", totalLessons);
		model.addAttribute("schedules", schedules);
		model.addAttribute("rowCount", schedules.size());
		model.addAttribute("groups", groupService.findAllGroups());
		model.addAttribute("classrooms", classroomService.findAllClassrooms());
		model.addAttribute("teachers", teacherService.findAllTeachers());
        return "list-schedules";
    }

	@GetMapping("/")
    public String getSchedules(@RequestParam(required = false) String groupTitle, 
	@RequestParam(required = false) String classroomTitle, @RequestParam(required = false) String teacherName, 
	@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, Model model) {
        if (startDate == null) {
            List<Schedule> allSchedules = scheduleService.getSchedulesSortedByGroupAndDateAndTime();
            if (!allSchedules.isEmpty()) {
                startDate = allSchedules.get(0).getDate().with(java.time.DayOfWeek.MONDAY);
            } else {
                startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            }
        }
        LocalDate endDate = startDate.plusDays(6);
		List<Schedule> schedules = getSchedulesForWeek(groupTitle, classroomTitle, teacherName, startDate, endDate);
        model.addAttribute("groups", groupService.findAllGroups());
		model.addAttribute("classrooms", classroomService.findAllClassrooms());
		model.addAttribute("teachers", teacherService.findAllTeachers());
        model.addAttribute("groups", groupService.findAllGroups());
		model.addAttribute("groupTitle", groupTitle);
        model.addAttribute("classroomTitle", classroomTitle);
        model.addAttribute("teacherName", teacherName);
        model.addAttribute("schedules", schedules);
        model.addAttribute("startDate", startDate);
        model.addAttribute("startTimes", startTimes);
		model.addAttribute("endTimes", endTimes);
        return "index";
    }

    private List<Schedule> getSchedulesForWeek(String groupTitle, String classroomTitle, String teacherName, LocalDate startDate, LocalDate endDate) {
        if (groupTitle != null && !groupTitle.isEmpty()) {
            return scheduleService.getSchedulesSortedByGroupAndDateAndTime(groupTitle);
		} else if (classroomTitle != null && !classroomTitle.isEmpty()) {
            return scheduleService.getSchedulesSortedByClassroomAndDateAndTime(classroomTitle);
		} else if (teacherName != null && !teacherName.isEmpty()) {
            return scheduleService.getSchedulesSortedByTeacherAndDateAndTime(teacherName);
        } else {
			String title = scheduleService.getTitleOfFirstGroup();
            return scheduleService.getSchedulesSortedByGroupAndDateAndTime(title); 
        }
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

	@GetMapping("/delete-schedule")
    public String deleteAllSchedules() {
        scheduleService.deleteAllSchedules();
		return "redirect:/schedules";
    }

}