package vea.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import vea.model.Course;
import vea.model.Teacher;
import vea.service.ClassroomService;
import vea.service.CourseService;
import vea.service.TeacherService;

@Controller
public class TeacherController {
	
    @Autowired
	private TeacherService teacherService;

	@Autowired
    private CourseService courseService;

	@Autowired
    private ClassroomService classroomService;

	private boolean isSorted = false;

	@GetMapping("/teachers")
	public String findAllTeachers(Model model) {
		List<Teacher> teachers = teacherService.findAllTeachers();
		model.addAttribute("teachers", teachers);
		model.addAttribute("rowCount", teachers.size());
		return "list-teachers";
	}

	@GetMapping("/search-teacher/{id}")
    public String getTeacherCourses(@PathVariable Long id, Model model) throws Exception {
		try {
			Teacher teacher = teacherService.findTeacherById(id);
            model.addAttribute("courses", courseService.getCoursesByTeacherId(id));
			model.addAttribute("teacher", teacher);
			model.addAttribute("name", teacher.getName());
			model.addAttribute("totalLessons", courseService.getTotalLessonsByTeacherId(id));
            return "list-teacher";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
    }

	@GetMapping("/search-teacher")
	public String searchTeachers(@Param("keyword") String keyword, Model model) {
		model.addAttribute("keyword", keyword);
		List<Teacher> teachers = teacherService.searchTeacherByName(keyword);
		model.addAttribute("teachers", teachers);
		model.addAttribute("rowCount", teachers.size());
		return "list-teachers";
	}

	@GetMapping("/teachers/sorted")
    public String getSortedTeachers(Model model) {
		List<Teacher> teachers = teacherService.getSortedTeachers(isSorted);
        model.addAttribute("teachers", teachers);
		model.addAttribute("rowCount", teachers.size());
        return "list-teachers";
    }

	@GetMapping("/teachers/toggleSort")
    public String toggleSort(Model model) {
        isSorted = !isSorted;
        return "redirect:/teachers/sorted";
    }

	@GetMapping("/add-teacher")
	public String showCreateForm(Teacher teacher, Model model) {
		try {
			model.addAttribute("classroom", classroomService.findAllClassrooms());
			return "add-teacher";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/add-teacher")
	public String createTeacher(@Valid Teacher teacher, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("classroom", classroomService.findAllClassrooms());
			return "add-teacher";
		}
		teacherService.createTeacher(teacher);
		model.addAttribute("teacher", teacherService.findAllTeachers());
		return "redirect:/teachers";
	}

	@GetMapping("/update-teacher/{id}")
	public String showUpdateForm(@PathVariable Long id, Model model) throws Exception {
		try {
		    model.addAttribute("teacher", teacherService.findTeacherById(id));
			model.addAttribute("classroom", classroomService.findAllClassrooms());
		    return "update-teacher";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/update-teacher/{id}")
	public String updateTeacher(@PathVariable Long id, @Valid Teacher teacher, BindingResult result, Model model) {
		if (result.hasErrors()) {
			teacher.setId(id);
			model.addAttribute("classroom", classroomService.findAllClassrooms());
			return "update-teacher";
		}
		teacherService.createTeacher(teacher);
		model.addAttribute("teacher", teacherService.findAllTeachers());
		return "redirect:/teachers";
	}

	@GetMapping("/remove-teacher/{id}")
	public String deleteTeacher(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) throws Exception {
		try {
			teacherService.deleteTeacher(id);
		    model.addAttribute("teacher", teacherService.findAllTeachers());
		    return "redirect:/teachers";
		} catch (Exception e) {
            if (e.getMessage().contains("Cannot delete or update a parent row")) {
                List<Course> connectedCourses = courseService.getCoursesByTeacherId(id);
				List<String> courseTitles = connectedCourses.stream().map(Course::getTitle).collect(Collectors.toList());
				redirectAttributes.addFlashAttribute("errormsg", "Nevar izdzēst pasniedzēju. Saistīts ar šādiem kursiem: " + String.join(", ", courseTitles));
				return "redirect:/teachers";
            } else {
                model.addAttribute("errormsg", e.getMessage());
				return "error-page";
            }
		}
	}
	
}