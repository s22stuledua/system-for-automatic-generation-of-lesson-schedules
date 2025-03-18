package vea.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vea.model.Classroom;
import vea.model.Course;
import vea.model.Equipment;
import vea.model.Schedule;
import vea.model.Semester;
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
    public List<Schedule> getSchedulesSortedByGroupAndDateAndTime() {
        return scheduleRepo.findAllOrderByGroupAndDateAndTime();
    }

    @Override
    public List<Schedule> getSchedulesSortedByGroupAndDateAndTime(String groupTitle) {
        return scheduleRepo.findAllOrderByGroupAndDateAndTime(groupTitle);
    }

    @Override
    public List<Schedule> getSchedulesSortedByClassroomAndDateAndTime(String classroomTitle) {
        return scheduleRepo.findAllOrderByClassroomAndDateAndTime(classroomTitle);
    }

    @Override
    public List<Schedule> getSchedulesSortedByTeacherAndDateAndTime(String teacherName) {
        return scheduleRepo.findAllOrderByTeacherAndDateAndTime(teacherName);
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
    public void generateSchedule(LocalDate startDate, Semester selectedSemester) {
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

        List<Group> lastSemesterGroups = groups.stream()
        .filter(group -> group.getLastSemester() && group.getSemester() == selectedSemester).collect(Collectors.toList());
        List<Group> otherGroups = groups.stream()
        .filter(group -> !group.getLastSemester() && group.getSemester() == selectedSemester).collect(Collectors.toList());

        for (Group group : lastSemesterGroups) {
            List<Schedule> schedule = scheduleForGroup(group, courses, classrooms, teacherAvailability, classroomAvailability, startDate, endDate, skipDates);
            newGroupSchedules.put(group, schedule);
            scheduleRepo.saveAll(schedule);
        }

        for (Group group : otherGroups) {
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
        Map<Integer, Map<Course, Integer>> courseWeeklyCount = new HashMap<>();
        Map<LocalDate, Set<LocalTime>> groupScheduleOnDate = new HashMap<>();

        List<Course> twoTeacherCourses = new ArrayList<>();
        List<Course> oneTeacherCourses = new ArrayList<>();
    
        for (Course course : courses) {
            if (!courseBelongsToGroup(course, group)) continue;
            if (course.hasTwoTeachers()) {
                twoTeacherCourses.add(course);
            } else {
                oneTeacherCourses.add(course);
            }
        }

        for (Course course : twoTeacherCourses) {
            scheduleCourseForGroup(group, course, classrooms, teacherAvailability, 
                                   classroomAvailability, startDate, endDate, 
                                   skipDates, scheduledLessons, predefinedTimes, 
                                   lessonsPerDate, lessonsPerWeek, courseWeeklyCount, 
                                   groupScheduleOnDate);
        }
    
        for (Course course : oneTeacherCourses) {
            scheduleCourseForGroup(group, course, classrooms, teacherAvailability, 
                                   classroomAvailability, startDate, endDate, 
                                   skipDates, scheduledLessons, predefinedTimes, 
                                   lessonsPerDate, lessonsPerWeek, courseWeeklyCount, 
                                   groupScheduleOnDate);
        }
        return scheduledLessons;
    }
    
    private void scheduleCourseForGroup(Group group, Course course, List<Classroom> classrooms,
                                    Map<Teacher, LocalDateTime> teacherAvailability,
                                    Map<Classroom, LocalDateTime> classroomAvailability,
                                    LocalDate startDate, LocalDate endDate, 
                                    Set<LocalDate> skipDates, List<Schedule> scheduledLessons,
                                    List<LocalTime> predefinedTimes, 
                                    Map<LocalDate, Integer> lessonsPerDate,
                                    Map<Integer, Integer> lessonsPerWeek,
                                    Map<Integer, Map<Course, Integer>> courseWeeklyCount,
                                    Map<LocalDate, Set<LocalTime>> groupScheduleOnDate) {
        LocalDate currentDate = startDate;
        int courseLimitPerWeek = getCourseLimitPerWeek(course);
        int courseLessonsScheduled = 0;

        while (currentDate.isBefore(endDate) && courseLessonsScheduled < course.getNumberOfLessons()) {
            if (skipDates.contains(currentDate) || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                currentDate = currentDate.plusDays(1);
                continue;
            }

            int weekNumber = currentDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            lessonsPerWeek.putIfAbsent(weekNumber, 0);
            courseWeeklyCount.putIfAbsent(weekNumber, new HashMap<>());
            Map<Course, Integer> currentCourseWeeklyCountMap = courseWeeklyCount.get(weekNumber);
            currentCourseWeeklyCountMap.putIfAbsent(course, 0);
            int currentLessonsPerWeek = lessonsPerWeek.getOrDefault(weekNumber, 0);
            int currentCourseWeeklyCount = currentCourseWeeklyCountMap.getOrDefault(course, 0);

            if (currentLessonsPerWeek < 15 && currentCourseWeeklyCount < courseLimitPerWeek) {
                for (LocalTime lessonStartTime : predefinedTimes) {
                    Set<LocalTime> scheduledTimes = groupScheduleOnDate.computeIfAbsent(currentDate, k -> new HashSet<>());

                    if (scheduledTimes.contains(lessonStartTime)) {
                        continue; 
                    }

                    int currentLessonsPerDate = lessonsPerDate.getOrDefault(currentDate, 0);

                    if (currentLessonsPerDate < 4) {
                        Teacher teacher = null;

                        if (course.hasTwoTeachers()) {
                            int phaseLength = (course.getNumberOfLessons() > 16) ? 4 : 2;
                            int phaseIndex = (courseLessonsScheduled / phaseLength) % 2;
                            teacher = (phaseIndex == 0) ? course.getTeacher1() : course.getTeacher2();
                        } else {
                            teacher = course.getTeacher1();
                        }

                        LocalDateTime lessonDateTime = LocalDateTime.of(currentDate, lessonStartTime);

                        if (isTeacherAvailable(teacher, lessonDateTime, teacherAvailability)) {
                            Classroom classroom = findAvailableClassroom(teacher, group, course, classrooms, currentDate, lessonStartTime);
                            if (classroom != null) {
                                Schedule lesson = new Schedule(group, course, teacher, classroom, currentDate, lessonStartTime); 
                                scheduledLessons.add(lesson);

                                updateTeacherAvailability(teacher, lessonDateTime, teacherAvailability);
                                lessonsPerDate.merge(currentDate, 1, Integer::sum);
                                lessonsPerWeek.merge(weekNumber, 1, Integer::sum);
                                currentCourseWeeklyCountMap.merge(course, 1, Integer::sum);
                                courseLessonsScheduled++;
                                scheduledTimes.add(lessonStartTime);
                                
                                if (courseLessonsScheduled >= course.getNumberOfLessons() ||
                                    lessonsPerDate.get(currentDate) >= 4 || lessonsPerWeek.get(weekNumber) >= 15 ||
                                    currentCourseWeeklyCountMap.get(course) >= courseLimitPerWeek) {
                                    break;
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }
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

    private int getCourseLimitPerWeek(Course course) {
        int totalLessons = course.getNumberOfLessons();
        if (totalLessons < 16) {
            return 2;
        } else if (totalLessons == 16) {
            return 4;
        } else {
            return 8;
        }
    }

    private boolean isTeacherAvailable(Teacher teacher, LocalDateTime lessonDateTime, 
                                   Map<Teacher, LocalDateTime> teacherAvailability) {
        LocalDateTime currentAvailability = teacherAvailability.get(teacher);
        return currentAvailability == null || lessonDateTime.isAfter(currentAvailability.plusMinutes(60));
    }
    
    private void updateTeacherAvailability(Teacher teacher, LocalDateTime lessonDateTime, 
                                           Map<Teacher, LocalDateTime> teacherAvailability) {
        teacherAvailability.put(teacher, lessonDateTime);
    }
    
    private Classroom findAvailableClassroom(Teacher teacher, Group group, Course course, List<Classroom> classrooms, LocalDate date, LocalTime time) {
        for (Classroom classroom : classrooms) {
            if (teacher.getUnteachableClassrooms().contains(classroom)) {
                continue;
            }
            if (classroom.getNumberOfSeats() < group.getNumberOfStudents()) {
                continue;
            }
            if (teacher.getOnlyOnline() && "Attālināti / Online".equals(classroom.getTitle())) {
                return classroom;
            }
            if (!teacher.getOnlyOnline() && isClassroomAvailable(classroom, teacher, course, date, time)) {
                if (course.hasTwoTeachers()) {
                    if (teacher.equals(course.getTeacher1())) {
                        if (matchesEquipment(classroom, course.getEquipment1(), course.getEquipment2(), course.getEquipment3(), course.getEquipment4())) {
                            return classroom;
                        }
                    } else if (teacher.equals(course.getTeacher2())) {
                        if (matchesEquipment(classroom, course.getEquipment5(), course.getEquipment6(), course.getEquipment7(), course.getEquipment8())) {
                            return classroom;
                        }
                    }
                } else {
                    if (matchesEquipment(classroom, course.getEquipment1(), course.getEquipment2(), course.getEquipment3(), course.getEquipment4())) {
                        return classroom;
                    }
                }
            }
        }
        if (requiresOnlyOnlineEquipment(teacher, course)) {
            for (Classroom classroom : classrooms) {
                if ("Attālināti / Online".equals(classroom.getTitle())) {
                    return classroom;
                }
            }
        }
        return null;
    }

    private boolean matchesEquipment(Classroom classroom, Equipment... requiredEquipments) {
        if (requiredEquipments == null) {
            return true;
        }
        for (Equipment equipment : requiredEquipments) {
            if (equipment != null && !containsEquipment(classroom, equipment)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsEquipment(Classroom classroom, Equipment equipment) {
        if (equipment == null) {
            return true;
        }
        return equipment.equals(classroom.equipment1) || equipment.equals(classroom.equipment2) ||
               equipment.equals(classroom.equipment3) || equipment.equals(classroom.equipment4);
    }

    private boolean requiresOnlyOnlineEquipment(Teacher teacher, Course course) {
        Equipment[] permittedEquipment = {Equipment.Datorklase, Equipment.Projektors, Equipment.Ekrāns, Equipment.Tāfele_interaktīvā};
        Equipment[] courseEquipment1 = {course.getEquipment1(), course.getEquipment2(), course.getEquipment3(), course.getEquipment4()};
        Equipment[] courseEquipment2 = {course.getEquipment5(), course.getEquipment6(), course.getEquipment7(), course.getEquipment8()};

        if (course.hasTwoTeachers()) {
            if (teacher.equals(course.getTeacher1())) {
                for (Equipment equipment : courseEquipment1) {
                    if (equipment != null && !ArrayContains(permittedEquipment, equipment)) {
                        return false;
                    }
                }
            } else if (teacher.equals(course.getTeacher2())) {
                for (Equipment equipment : courseEquipment2) {
                    if (equipment != null && !ArrayContains(permittedEquipment, equipment)) {
                        return false;
                    }
                }
            } 
        } else {
            for (Equipment equipment : courseEquipment1) {
                if (equipment != null && !ArrayContains(permittedEquipment, equipment)) {
                    return false;
                }
            }
        } 
        return true;
    }

    private boolean ArrayContains(Equipment[] array, Equipment equipment) {
        for (Equipment e : array) {
            if (e == equipment) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isClassroomAvailable(Classroom classroom, Teacher teacher, Course course, LocalDate date, LocalTime time) {
        List<Schedule> existingSchedules = scheduleRepo.findSchedulesByClassroomAndDateAndTime(classroom, date, time);
        if ("Attālināti / Online".equals(classroom.getTitle())) {
            return true;
        }
        return existingSchedules.isEmpty();
    }

}