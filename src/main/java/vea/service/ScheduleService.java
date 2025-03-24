package vea.service;

import java.time.LocalDate;
import java.util.List;
import vea.model.Schedule;
import vea.model.Semester;

public interface ScheduleService {

    public List<Schedule> findAllSchedules();

    public Schedule findLessonById(Long id) throws Exception;

    public List<Schedule> getSchedulesSortedByGroupAndDateAndTime();

    public List<Schedule> getSchedulesSortedByGroupAndDateAndTime(String groupTitle);

    public List<Schedule> getSchedulesSortedByClassroomAndDateAndTime(String classroomTitle);

    public List<Schedule> getSchedulesSortedByTeacherAndDateAndTime(String teacherName);

    public String getTitleOfFirstGroup();
    
    public void generateSchedule(LocalDate startDate, Semester selectedSemester);
    
    public void createLesson(Schedule lesson);

    public void deleteLesson(Long id) throws Exception;
    
}