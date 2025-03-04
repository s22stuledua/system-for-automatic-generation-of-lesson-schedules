package vea.service;

import java.time.LocalDate;
import java.util.List;
import vea.model.Schedule;

public interface ScheduleService {

    public List<Schedule> findAllSchedules();

    public Schedule findLessonById(Long id) throws Exception;

    public List<Schedule> getSchedulesSortedByGroupAndDateAndTime();

    public List<Schedule> getSchedulesSortedByGroupAndDateAndTime(String groupTitle);
    
    public void generateSchedule(LocalDate startDate);
    
    public void createLesson(Schedule lesson);

    public void deleteLesson(Long id) throws Exception;
    
}