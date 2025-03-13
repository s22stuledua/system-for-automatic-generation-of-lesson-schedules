package vea.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import vea.model.Holiday;
import vea.service.HolidayService;

@Controller
public class HolidayController {

	@Autowired
    private HolidayService holidayService;

    @GetMapping("/holidays")
	public String findAllHolidays(Model model) {
		List<Holiday> holidays = holidayService.findAllHolidays();
		model.addAttribute("holidays", holidays);
		model.addAttribute("rowCount", holidays.size());
		return "list-holidays";
	}

    @GetMapping("/add-holiday")
	public String showCreateForm(Holiday holiday, Model model) {
		try {
			return "add-holiday";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/add-holiday")
	public String createHoliday(@Valid Holiday holiday, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "add-holiday";
		}
		holidayService.createHoliday(holiday);
		model.addAttribute("holiday", holidayService.findAllHolidays());
		return "redirect:/holidays";
	}

	@GetMapping("/update-holiday/{id}")
	public String showUpdateForm(@PathVariable Long id, Model model) throws Exception {
		try {
		    model.addAttribute("holiday", holidayService.findHolidayById(id));
		    return "update-holiday";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

	@PostMapping("/update-holiday/{id}")
	public String updateHoliday(@PathVariable Long id, @Valid Holiday holiday, BindingResult result, Model model) {
		if (result.hasErrors()) {
			holiday.setId(id);
			return "update-holiday";
		}
		holidayService.createHoliday(holiday);
		model.addAttribute("holiday", holidayService.findAllHolidays());
		return "redirect:/holidays";
	}

    @GetMapping("/remove-holiday/{id}")
	public String deleteHoliday(@PathVariable Long id, Model model) throws Exception {
		try {
			holidayService.deleteHoliday(id);
		    model.addAttribute("holiday", holidayService.findAllHolidays());
		    return "redirect:/holidays";
		} catch (Exception e) {
			model.addAttribute("errormsg", e.getMessage());
			return "error-page";
		}
	}

}