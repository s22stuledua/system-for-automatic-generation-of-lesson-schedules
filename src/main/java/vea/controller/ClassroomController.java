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
import vea.model.Classroom;
import vea.service.ClassroomService;

@Controller
public class ClassroomController {
	
    @Autowired
	private ClassroomService classroomService;

	@GetMapping("/classrooms")
	public String findAllClassrooms(Model model) {
		List<Classroom> classrooms = classroomService.findAllClassrooms();
		model.addAttribute("classrooms", classrooms);
		return "list-classrooms";
	}

	@GetMapping("/search-classroom")
	public String searchClassroom(@Param("keyword") String keyword, Model model) {
		List<Classroom> classroom = classroomService.searchClassroomByTitle(keyword);
		model.addAttribute("classrooms", classroom);
		model.addAttribute("keyword", keyword);
		return "list-classrooms";
	}

	@GetMapping("/add-classroom")
	public String showCreateForm(Classroom classroom, Model model) {
		try {
			return "add-classroom";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/add-classroom")
	public String createClassroom(@Valid Classroom classroom, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "add-classroom";
		}
		classroomService.createClassroom(classroom);
		model.addAttribute("classroom", classroomService.findAllClassrooms());
		return "redirect:/classrooms";
	}

	@GetMapping("/update-classroom/{id}")
	public String showUpdateForm(@PathVariable Long id, Model model) throws Exception {
		try {
			Classroom classroom = classroomService.findClassroomById(id);
		    model.addAttribute("classroom", classroom);
		    return "update-classroom";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/update-classroom/{id}")
	public String updateClassroom(@PathVariable Long id, @Valid Classroom classroom, BindingResult result, Model model) {
		if (result.hasErrors()) {
			classroom.setId(id);
			return "update-classroom";
		}
		classroomService.updateClassroom(classroom);
		model.addAttribute("classroom", classroomService.findAllClassrooms());
		return "redirect:/classrooms";
	}

	@GetMapping("/remove-classroom/{id}")
	public String deleteClassroom(@PathVariable Long id, Model model) throws Exception {
		try {
			classroomService.deleteClassroom(id);
		    model.addAttribute("classroom", classroomService.findAllClassrooms());
		    return "redirect:/classrooms";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

}