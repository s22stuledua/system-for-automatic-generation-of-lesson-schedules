package vea.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vea.model.Classroom;
import vea.model.Course;
import vea.model.Schedule;
import vea.model.Group;
import vea.model.Holiday;
import vea.model.Teacher;
import vea.repo.ClassroomRepo;
import vea.repo.CourseRepo;
import vea.repo.GroupRepo;
import vea.repo.HolidayRepo;
import vea.repo.ScheduleRepo;
import vea.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepo scheduleRepo;
    
    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private ClassroomRepo classroomRepo;

    @Autowired
    private HolidayRepo holidayRepo;

    @Override
    public List<Schedule> findAllSchedules() {
        List<Schedule> schedules = scheduleRepo.findAll();
        return schedules != null ? schedules : new ArrayList<>();
    }

    @Override
    public Schedule findLessonById(Long id) throws Exception {
        return scheduleRepo.findById(id).orElseThrow(() -> new Exception("Lekcija ar šo ID netika atrasta"));
    }

    @Override
    public List<Schedule> getSchedulesByGroupTitle(String groupTitle) {
        return scheduleRepo.findByGroup_Title(groupTitle);
    }

    @Override
	public void createLesson(Schedule lesson) {
		scheduleRepo.save(lesson);
	}

    @Override
	public void deleteLesson(Long id) throws Exception {
		Schedule lesson = scheduleRepo.findById(id).orElseThrow(() -> new Exception("Lekcija ar šo ID netika atrasta"));
		scheduleRepo.deleteById(lesson.getId());
	}

    @Override
    public void generateSchedule(LocalDate startDate) {
        scheduleRepo.deleteAll();

        List<Group> groups = groupRepo.findAll();
        List<Course> courses = courseRepo.findAll();
        List<Classroom> classrooms = classroomRepo.findAll();
        List<Holiday> holidays = holidayRepo.findAll();
        Set<LocalDate> skipDates = holidays.stream().map(Holiday::getDate).collect(Collectors.toSet());

        Map<Group, List<Schedule>> newGroupSchedules = new HashMap<>();
        Map<Teacher, LocalDateTime> teacherAvailability = new HashMap<>();
        Map<Classroom, LocalDateTime> classroomAvailability = new HashMap<>();
        
        LocalDate endDate = startDate.plusWeeks(16);

        for (Group group : groups) {
            List<Schedule> schedule = scheduleForGroup(group, courses, classrooms, teacherAvailability, classroomAvailability, startDate, endDate, skipDates);
            newGroupSchedules.put(group, schedule);
            scheduleRepo.saveAll(schedule);
        }
    }

    private List<Schedule> scheduleForGroup(Group group, List<Course> courses, List<Classroom> classrooms,
                                    Map<Teacher, LocalDateTime> teacherAvailability,
                                    Map<Classroom, LocalDateTime> classroomAvailability,
                                    LocalDate startDate, LocalDate endDate, Set<LocalDate> skipDates) {
        List<Schedule> scheduledLessons = new ArrayList<>();
        List<LocalTime> predefinedTimes = Arrays.asList(
                LocalTime.of(9, 0),
                LocalTime.of(10, 40),
                LocalTime.of(13, 0),
                LocalTime.of(14, 40),
                LocalTime.of(16, 15),
                LocalTime.of(17, 50),
                LocalTime.of(19, 25)
        );
    
        Map<LocalDate, Integer> lessonsPerDate = new HashMap<>();
        Map<Integer, Integer> lessonsPerWeek = new HashMap<>();
    
        for (Course course : courses) {
            if (!courseBelongsToGroup(course, group)) continue;
            LocalDate currentDate = startDate;
    
            while (currentDate.isBefore(endDate)) {
                if (skipDates.contains(currentDate) || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    currentDate = currentDate.plusDays(1);
                    continue;
                }
    
                int weekNumber = currentDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                lessonsPerWeek.putIfAbsent(weekNumber, 0);
    
                if (lessonsPerWeek.get(weekNumber) < 15) {
                    for (LocalTime lessonStartTime : predefinedTimes) {
                        if (lessonsPerDate.getOrDefault(currentDate, 0) < 5) {
                            Schedule lesson = new Schedule(group, course, course.getTeacher1(), classrooms.get(1), currentDate, lessonStartTime);
                            scheduledLessons.add(lesson);
                            
                            lessonsPerDate.merge(currentDate, 1, Integer::sum);
                            lessonsPerWeek.merge(weekNumber, 1, Integer::sum);
    
                            if (lessonsPerDate.get(currentDate) >= 5 || lessonsPerWeek.get(weekNumber) >= 15) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }
        return scheduledLessons;
    }

    private boolean courseBelongsToGroup(Course course, Group group) {
        if (group == null || course == null) {
            return false;
        }
        Course[] courses = {group.getCourse1(), group.getCourse2(), group.getCourse3(), group.getCourse4(),
                            group.getCourse5(), group.getCourse6(), group.getCourse7(), group.getCourse8(),
                            group.getCourse9(), group.getCourse10(), group.getCourse11()};
        
        for (Course currentCourse : courses) {
            if (currentCourse != null && currentCourse.equals(course)) {
                return true;
            }
        }
        return false;
    }

}