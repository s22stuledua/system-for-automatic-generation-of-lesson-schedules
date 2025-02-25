package vea.service;

import java.util.List;
import vea.model.Schedule;

public interface ScheduleService {

    public List<Schedule> findAllSchedules();

    public Schedule findLessonById(Long id) throws Exception;

    public List<Schedule> getSchedulesByGroupTitle(String groupTitle);

    public List<Schedule> getSchedulesSortedByGroupAndTime();
    
    public void generateSchedule();
    
    public void createLesson(Schedule lesson);

    public void updateLesson(Schedule lesson);

    public void deleteLesson(Long id) throws Exception;
    
}