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
import vea.model.Course;
import vea.model.Group;
import vea.service.CourseService;
import vea.service.GroupService;
import vea.service.TeacherService;

@Controller
public class CourseController {
	
    @Autowired
	private CourseService courseService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private GroupService groupService;

	private boolean isSorted = false;

	@GetMapping("/courses")
	public String findAllCourses(Model model) {
		List<Course> courses = courseService.findAllCourses();
		model.addAttribute("courses", courses);
		return "list-courses";
	}

	@GetMapping("/search-course")
	public String searchCourse(@Param("keyword") String keyword, Model model) {
		List<Course> course = courseService.searchCourseByTitle(keyword);
		model.addAttribute("courses", course);
		model.addAttribute("keyword", keyword);
		return "list-courses";
	}

	@GetMapping("/search-course/{id}")
	public String findCoursepById(@PathVariable("id") Long id, Model model) throws Exception {
		try {
			Course course = courseService.findCourseById(id);
		    model.addAttribute("courses", course);
		    return "list-course";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@GetMapping("/courses/sorted")
    public String getSortedCourses(Model model) {
		List<Course> course = courseService.getSortedCourses(isSorted);
        model.addAttribute("courses", course);
        return "list-courses";
    }

	@GetMapping("/courses/toggleSort")
    public String toggleSort(Model model) {
        isSorted = !isSorted;
        return "redirect:/courses/sorted";
    }

	@GetMapping("/course/{id}")
	public String findCourseById(@PathVariable("id") Long id, Model model) throws Exception {
		try {
			Course course = courseService.findCourseById(id);
		    model.addAttribute("course", course);
		    return "list-course";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@GetMapping("/add-course")
	public String showCreateForm(Course course, Model model) {
		try {
			model.addAttribute("teacher", teacherService.findAllTeachers());
			return "add-course";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/add-course")
	public String createCourse(Course course, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "add-course";
		}
		courseService.createCourse(course);
		model.addAttribute("course", courseService.findAllCourses());
		return "redirect:/courses";
	}

	@GetMapping("/update-course/{id}")
	public String showUpdateForm(@PathVariable("id") Long id, Model model) throws Exception {
		try {
			Course course = courseService.findCourseById(id);
		    model.addAttribute("course", course);
			model.addAttribute("teacher", teacherService.findAllTeachers());
		    return "update-course";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@GetMapping("/edit-course/{id}")
	public String showEditForm(@PathVariable("id") Long id, Model model) throws Exception {
		try {
			Course course = courseService.findCourseById(id);
		    model.addAttribute("course", course);
			model.addAttribute("teacher", teacherService.findAllTeachers());
		    return "edit-course";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/update-course/{id}")
	public String updateCourse(@PathVariable("id") Long id, Course course, BindingResult result, Model model) {
		if (result.hasErrors()) {
			course.setId(id);
			return "update-course";
		}
		courseService.updateCourse(course);
		model.addAttribute("course", courseService.findAllCourses());
		return "redirect:/courses";
	}

	@GetMapping("/remove-course/{id}")
	public String deleteCourse(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) throws Exception {
		try {
			courseService.deleteCourse(id);
		    model.addAttribute("course", courseService.findAllCourses());
		    return "redirect:/courses";
		} catch (Exception e) {
            if (e.getMessage().contains("Cannot delete or update a parent row")) {
                List<Group> connectedGroups = groupService.getGroupsByCourseId(id);
				List<String> groupTitles = connectedGroups.stream().map(Group::getTitle).collect(Collectors.toList());
				redirectAttributes.addFlashAttribute("errormsg", "Nevar izdzēst kursu. Saistīts ar šādam grupam: " + String.join(", ", groupTitles));
				return "redirect:/courses";
            } else {
                model.addAttribute("errormsg", e.getMessage());
				return "error-page";
            }
		}
	}

}