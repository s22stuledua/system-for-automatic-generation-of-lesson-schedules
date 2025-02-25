package vea.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import vea.model.Group;
import vea.service.CourseService;
import vea.service.GroupService;

@Controller
public class GroupController {
	
    @Autowired
	private GroupService groupService;

	@Autowired
	private CourseService courseService;

	@GetMapping("/groups")
	public String findAllGroups(Model model) {
		List<Group> groups = groupService.findAllGroups();
		model.addAttribute("groups", groups);
		return "list-groups";
	}

	@GetMapping("/search-group")
	public String searchGroup(@Param("keyword") String keyword, Model model) {
		List<Group> group = groupService.searchGroupByTitle(keyword);
		model.addAttribute("groups", group);
		model.addAttribute("keyword", keyword);
		return "list-groups";
	}

	@GetMapping("/search-group/{id}")
	public String findGroupById(@PathVariable Long id, Model model) throws Exception {
		try {
			Group group = groupService.findGroupById(id);
		    model.addAttribute("groups", group);
		    return "list-group";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@GetMapping("/add-group")
	public String showCreateForm(Group group, Model model) {
		try {
			model.addAttribute("course", courseService.findAllCourses());
			return "add-group";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/add-group")
	public String createGroup(@Valid Group group, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("course", courseService.findAllCourses());
			return "add-group";
		}
		groupService.createGroup(group);
		model.addAttribute("group", groupService.findAllGroups());
		return "redirect:/groups";
	}

	@GetMapping("/update-group/{id}")
	public String showUpdateForm(@PathVariable Long id, Model model) throws Exception {
		try {
			Group group = groupService.findGroupById(id);
		    model.addAttribute("group", group);
			model.addAttribute("course", courseService.findAllCourses());
		    return "update-group";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@GetMapping("/edit-group/{id}")
	public String showEditForm(@PathVariable Long id, Model model) throws Exception {
		try {
			Group group = groupService.findGroupById(id);
		    model.addAttribute("group", group);
			model.addAttribute("course", courseService.findAllCourses());
		    return "edit-group";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/update-group/{id}")
	public String updateGroup(@PathVariable Long id, @Valid Group group, BindingResult result, Model model) {
		if (result.hasErrors()) {
			group.setId(id);
			model.addAttribute("course", courseService.findAllCourses());
			return "update-group";
		}
		groupService.updateGroup(group);
		model.addAttribute("group", groupService.findAllGroups());
		return "redirect:/groups";
	}

	@GetMapping("/remove-group/{id}")
	public String deleteGroup(@PathVariable Long id, Model model) throws Exception {
		try {
			groupService.deleteGroup(id);
		    model.addAttribute("group", groupService.findAllGroups());
		    return "redirect:/groups";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}
	
}