package vea.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vea.model.Holiday;
import vea.repo.HolidayRepo;
import vea.service.HolidayService;

@Service
public class HolidayServiceImpl implements HolidayService {
    
    @Autowired
	private HolidayRepo holidayRepo;

	@Override
	public List<Holiday> findAllHolidays() {
		return holidayRepo.findAll();
	}

	@Override
	public Holiday findHolidayById(Long id) throws Exception {
		return holidayRepo.findById(id).orElseThrow(() -> new Exception("Brīvdiena ar šo ID netika atrasta"));
	}

	@Override
	public void createHoliday(Holiday holiday) {
		holidayRepo.save(holiday);
	}

	@Override
	public void deleteHoliday(Long id) throws Exception {
		Holiday holiday = holidayRepo.findById(id).orElseThrow(() -> new Exception("Brīvdiena ar šo ID nav atrasta"));
		holidayRepo.deleteById(holiday.getId());
	}

}