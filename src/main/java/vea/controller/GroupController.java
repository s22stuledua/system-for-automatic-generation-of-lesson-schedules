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
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import vea.model.Group;
import vea.model.Semester;
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
		model.addAttribute("rowCount", groups.size());
		return "list-groups";
	}

	@GetMapping("/search-group")
	public String searchGroup(@Param("keyword") String keyword, Model model) {
		model.addAttribute("keyword", keyword);
		List<Group> groups = groupService.searchGroupByTitle(keyword);
		model.addAttribute("groups", groups);
		model.addAttribute("rowCount", groups.size());
		return "list-groups";
	}

	@GetMapping("/search-group/{id}")
	public String findGroupById(@PathVariable Long id, Model model) throws Exception {
		try {
		    model.addAttribute("groups", groupService.findGroupById(id));
		    return "list-group";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@GetMapping("/groups/sorted")
    public String showGroups(@RequestParam(required = false) String semester, 
	@RequestParam(required = false) Boolean filterLastSemester, Model model) {
        List<Group> groups = groupService.findAllGroups();
		if (semester != null && !semester.isEmpty()) {
            Semester selectedSemester = Semester.valueOf(semester);
            groups = groupService.getGroupsSortedBySemester(selectedSemester);
		}
		if (filterLastSemester != null && filterLastSemester) {
			groups = groups.stream().filter(Group::getLastSemester).collect(Collectors.toList());
		} 
        model.addAttribute("groups", groups);
		model.addAttribute("rowCount", groups.size());
		model.addAttribute("semester", semester);
		model.addAttribute("filterLastSemester", filterLastSemester != null && filterLastSemester);
        return "list-groups";
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
		    model.addAttribute("group", groupService.findGroupById(id));
			model.addAttribute("course", courseService.findAllCourses());
		    return "update-group";
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
		groupService.createGroup(group);
		model.addAttribute("group", groupService.findAllGroups());
		return "redirect:/groups";
	}

	@GetMapping("/edit-group/{id}")
	public String showEditForm(@PathVariable Long id, Model model) throws Exception {
		try {
		    model.addAttribute("group", groupService.findGroupById(id));
			model.addAttribute("course", courseService.findAllCourses());
		    return "edit-group";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/edit-group/{id}")
	public String editGroup(@PathVariable Long id, @Valid Group group, BindingResult result, Model model) {
		if (result.hasErrors()) {
			group.setId(id);
			model.addAttribute("course", courseService.findAllCourses());
			return "edit-group";
		}
		groupService.createGroup(group);
		model.addAttribute("group", groupService.findAllGroups());
		return "redirect:/search-group/" + id;
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