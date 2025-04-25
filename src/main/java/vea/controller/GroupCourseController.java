package vea.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import vea.model.GroupCourse;
import vea.service.CourseService;
import vea.service.GroupCourseService;
import vea.service.GroupService;

@Controller
public class GroupCourseController {

    @Autowired
	private GroupCourseService groupCourseService;

    @Autowired
	private GroupService groupService;

	@Autowired
	private CourseService courseService;

    @GetMapping("/group_courses")
	public String findAllGroupCourses(Model model) {
		List<GroupCourse> groupCourses = groupCourseService.findAllGroupCourses();
		model.addAttribute("groupCourses", groupCourses);
		model.addAttribute("rowCount", groupCourses.size());
		return "list-group_courses";
	}

    @GetMapping("/add-group_course")
	public String showCreateForm(@ModelAttribute GroupCourse groupCourse, Model model) {
		try {
            model.addAttribute("groups", groupService.findAllGroups());
			model.addAttribute("courses", courseService.findAllCourses());
			return "add-group_course";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/add-group_course")
	public String createGroupCourse(@Valid GroupCourse groupCourse, BindingResult result, Model model) {
		if (result.hasErrors()) {
            model.addAttribute("groups", groupService.findAllGroups());
			model.addAttribute("courses", courseService.findAllCourses());
			return "add-group_course";
		}
		groupCourseService.createGroupCourse(groupCourse);
		model.addAttribute("group_course", groupCourseService.findAllGroupCourses());
		return "redirect:/group_courses";
	}

	@GetMapping("/update-group_course/{id}")
	public String showUpdateForm(@PathVariable Long id, Model model) throws Exception {
		try {
		    model.addAttribute("group_course", groupCourseService.findGroupCourseById(id));
            model.addAttribute("group", groupService.findAllGroups());
			model.addAttribute("course", courseService.findAllCourses());
		    return "update-group_course";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/update-group_course/{id}")
	public String updateGroupCourse(@PathVariable Long id, @Valid GroupCourse groupCourse, BindingResult result, Model model) {
		if (result.hasErrors()) {
			groupCourse.setId(id);
            model.addAttribute("group", groupService.findAllGroups());
			model.addAttribute("course", courseService.findAllCourses());
			return "update-group_course";
		}
		groupCourseService.createGroupCourse(groupCourse);
        model.addAttribute("group_course", groupCourseService.findAllGroupCourses());
		return "redirect:/group_courses";
	}

    @GetMapping("/remove-group_course/{id}")
	public String deleteGroupCourse(@PathVariable Long id, Model model) throws Exception {
		try {
			groupCourseService.deleteGroupCourse(id);
		    model.addAttribute("group_course", groupCourseService.findAllGroupCourses());
		    return "redirect:/group_courses";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}
    
}