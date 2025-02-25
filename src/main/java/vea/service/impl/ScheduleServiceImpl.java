package vea.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vea.model.Classroom;
import vea.model.Course;
import vea.model.Schedule;
import vea.model.Group;
import vea.model.Teacher;
import vea.repo.ClassroomRepo;
import vea.repo.CourseRepo;
import vea.repo.GroupRepo;
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
    public List<Schedule> getSchedulesSortedByGroupAndTime() {
        return scheduleRepo.findAllOrderByGroupAndTime();
    }

    @Override
	public void createLesson(Schedule lesson) {
		scheduleRepo.save(lesson);
	}

    @Override
	public void updateLesson(Schedule lesson) {
		scheduleRepo.save(lesson);
	}

    @Override
	public void deleteLesson(Long id) throws Exception {
		Schedule lesson = scheduleRepo.findById(id).orElseThrow(() -> new Exception("Lekcija ar šo ID netika atrasta"));
		scheduleRepo.deleteById(lesson.getId());
	}

    @Override
    public void generateSchedule() {
        scheduleRepo.deleteAll();

        List<Group> groups = groupRepo.findAll();
        List<Course> courses = courseRepo.findAll();
        List<Classroom> classrooms = classroomRepo.findAll();

        Map<Group, List<Schedule>> newGroupSchedules = new HashMap<>();
        Map<Teacher, LocalDateTime> teacherAvailability = new HashMap<>();
        Map<Classroom, LocalDateTime> classroomAvailability = new HashMap<>();
        
        LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), Month.SEPTEMBER, 1);
        LocalDate endDate = startDate.plusWeeks(16);

        for (Group group : groups) {
            List<Schedule> schedule = scheduleForGroup(group, courses, classrooms, teacherAvailability, classroomAvailability, startDate, endDate);
            newGroupSchedules.put(group, schedule);
            scheduleRepo.saveAll(schedule);
        }
    }

    private List<Schedule> scheduleForGroup(Group group, List<Course> courses, List<Classroom> classrooms,
                                        Map<Teacher, LocalDateTime> teacherAvailability,
                                        Map<Classroom, LocalDateTime> classroomAvailability,
                                        LocalDate startDate, LocalDate endDate) {
        List<Schedule> scheduledLessons = new ArrayList<>();
    
        for (Course course : courses) {
            if (!courseBelongsToGroup(course, group)) continue;
            LocalDate currentDate = startDate;
            List<LocalTime> predefinedTimes = Arrays.asList(
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 40),
                        LocalTime.of(13, 0),
                        LocalTime.of(14, 40),
                        LocalTime.of(16, 15),
                        LocalTime.of(17, 50),
                        LocalTime.of(19, 25)
            );
            for (LocalTime lessonStartTime : predefinedTimes) {
                LocalDate lessonDate = currentDate;
                LocalTime lessonTime = lessonStartTime;
                Schedule lesson = new Schedule(group, course, course.getTeacher1(), classrooms.get(1), lessonDate, lessonTime);
                scheduledLessons.add(lesson);
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