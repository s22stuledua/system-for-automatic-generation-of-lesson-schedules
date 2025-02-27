package vea.service;

import java.util.List;
import vea.model.Holiday;

public interface HolidayService {
    
    public List<Holiday> findAllHolidays();

	public Holiday findHolidayById(Long id) throws Exception;

	public void createHoliday(Holiday holiday);

	public void deleteHoliday(Long id) throws Exception;

}