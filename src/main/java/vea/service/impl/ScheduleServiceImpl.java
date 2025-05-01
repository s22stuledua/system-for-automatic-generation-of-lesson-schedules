package vea.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import vea.model.GroupCourse;
import vea.model.Holiday;
import vea.model.Teacher;
import vea.model.TeacherUnavailability;
import vea.repo.ClassroomRepo;
import vea.repo.CourseRepo;
import vea.repo.GroupCourseRepo;
import vea.repo.GroupRepo;
import vea.repo.HolidayRepo;
import vea.repo.ScheduleRepo;
import vea.repo.TeacherUnavailabilityRepo;
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
    private GroupCourseRepo groupCourseRepo;

    @Autowired
    private ClassroomRepo classroomRepo;

    @Autowired
    private HolidayRepo holidayRepo;

    @Autowired
    private TeacherUnavailabilityRepo teacherUnavailabilityRepo;

    private static final int MAX_LESSONS_PER_WEEK = 15;
    private static final int MAX_LESSONS_PER_DATE = 4;

    private Map<Group, Map<LocalDate, Integer>> lessonsPerDateMap = new HashMap<>();
    private Map<Group, Map<Integer, Integer>> lessonsPerWeekMap = new HashMap<>();

    List<Schedule> scheduledLessons = new ArrayList<>();

    private Map<Group, Set<Course>> scheduledCoursesPerGroup = new HashMap<>();

    List<LocalTime> predefinedTimes = Arrays.asList(
        LocalTime.of(9, 0),
        LocalTime.of(10, 40),
        LocalTime.of(13, 0),
        LocalTime.of(14, 40),
        LocalTime.of(16, 15),
        LocalTime.of(17, 50),
        LocalTime.of(19, 25)
    );

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
    public String getTitleOfFirstGroup() {
        List<Schedule> schedules = scheduleRepo.findAll();
        if (!schedules.isEmpty()) {
            Group group = schedules.get(0).getGroup();
            if (group != null) {
                return group.getTitle();
            }
        }
        return null;
    }

    @Override
    public int calculateGroupLessons(String groupTitle) {
        List<Schedule> schedules = getSchedulesSortedByGroupAndDateAndTime(groupTitle);
        if (schedules.isEmpty()) {
            return 0;
        }
        Group group = schedules.get(0).getGroup();
        return group.calculateTotalLessons();
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
    public void deleteAllSchedules() {
        scheduleRepo.deleteAll();
    }

    @Override
    public void generateSchedule(LocalDate startDate, Semester selectedSemester) {
        scheduleRepo.deleteAll();

        List<Group> groups = groupRepo.findAll();
        List<Course> courses = courseRepo.findAll();
        List<GroupCourse> groupCourses = groupCourseRepo.findAll();
        List<Classroom> classrooms = classroomRepo.findAll();
        List<Holiday> holidays = holidayRepo.findAll();
        Set<LocalDate> skipDates = holidays.stream().map(Holiday::getDate).collect(Collectors.toSet());

        Map<Group, List<Schedule>> newGroupSchedules = new HashMap<>();
        Map<Teacher, LocalDateTime> teacherAvailability = new HashMap<>();
        Map<Classroom, LocalDateTime> classroomAvailability = new HashMap<>();
        Map<Group, Map<LocalDate, Set<LocalTime>>> occupiedTimes = new HashMap<>();
        
        LocalDate endDate = startDate.plusWeeks(16);

        for (GroupCourse groupCourse : groupCourses) {
            Group group1 = groupCourse.getGroup1();
            Group group2 = groupCourse.getGroup2();
            Course course = groupCourse.getCourse();

            List<Schedule> schedule = scheduleCourseForGroupCourse(group1, group2, course, classrooms, teacherAvailability, 
            classroomAvailability, startDate, endDate, skipDates);
            newGroupSchedules.put(group1, schedule);
            newGroupSchedules.put(group2, schedule);
            scheduleRepo.saveAll(schedule);
            scheduledCoursesPerGroup.putIfAbsent(group1, new HashSet<>());
            scheduledCoursesPerGroup.putIfAbsent(group2, new HashSet<>());
            scheduledCoursesPerGroup.get(group1).add(course);
            scheduledCoursesPerGroup.get(group2).add(course);
            occupiedTimes.putIfAbsent(group1, new HashMap<>());
            occupiedTimes.putIfAbsent(group2, new HashMap<>());
            recordOccupiedTimes(schedule, occupiedTimes.get(group1));
            recordOccupiedTimes(schedule, occupiedTimes.get(group2));
        }

        List<Group> lastSemesterGroups = groups.stream()
        .filter(group -> group.getLastSemester() && group.getSemester() == selectedSemester).collect(Collectors.toList());
        List<Group> otherGroups = groups.stream()
        .filter(group -> !group.getLastSemester() && group.getSemester() == selectedSemester).collect(Collectors.toList());

        for (Group group : lastSemesterGroups) {
            List<Schedule> schedule = scheduleForGroup(group, courses, classrooms, teacherAvailability, 
            classroomAvailability, startDate, endDate, skipDates, occupiedTimes.get(group));
            newGroupSchedules.put(group, schedule);
            scheduleRepo.saveAll(schedule);
        }

        for (Group group : otherGroups) {
            List<Schedule> schedule = scheduleForGroup(group, courses, classrooms, teacherAvailability, 
            classroomAvailability, startDate, endDate, skipDates, occupiedTimes.get(group));
            newGroupSchedules.put(group, schedule);
            scheduleRepo.saveAll(schedule);
        }
    }

    private List<Schedule> scheduleCourseForGroupCourse(Group group1, Group group2, Course course, List<Classroom> classrooms,
                                    Map<Teacher, LocalDateTime> teacherAvailability, Map<Classroom, LocalDateTime> classroomAvailability,
                                    LocalDate startDate, LocalDate endDate, Set<LocalDate> skipDates) {
        List<Schedule> scheduledLessons = new ArrayList<>();
        Map<LocalDate, Integer> lessonsPerDate = lessonsPerDateMap.computeIfAbsent(group1, k -> new HashMap<>());
        Map<Integer, Integer> lessonsPerWeek = lessonsPerWeekMap.computeIfAbsent(group1, k -> new HashMap<>());
        Map<Integer, Map<Course, Integer>> courseWeeklyCount = new HashMap<>();
        Map<Integer, Map<Course, Integer>> courseTwoWeekCount = new HashMap<>();
        Map<LocalDate, Set<LocalTime>> groupScheduleOnDate = new HashMap<>();
    
        int combinedNumberOfStudents = group1.getNumberOfStudents() + group2.getNumberOfStudents();
        
        scheduleCourseForGroups(group1, group2, course, classrooms, teacherAvailability, classroomAvailability,
        startDate, endDate, skipDates, scheduledLessons, predefinedTimes, lessonsPerDate,
        lessonsPerWeek, courseWeeklyCount, courseTwoWeekCount, groupScheduleOnDate, combinedNumberOfStudents);

        lessonsPerDateMap.put(group2, new HashMap<>(lessonsPerDate));
        lessonsPerWeekMap.put(group2, new HashMap<>(lessonsPerWeek));
    
        return scheduledLessons;
    }

    private void scheduleCourseForGroups(Group group1, Group group2, Course course, List<Classroom> classrooms,
                                    Map<Teacher, LocalDateTime> teacherAvailability, Map<Classroom, LocalDateTime> classroomAvailability,
                                    LocalDate startDate, LocalDate endDate, Set<LocalDate> skipDates, List<Schedule> scheduledLessons,
                                    List<LocalTime> predefinedTimes, Map<LocalDate, Integer> lessonsPerDate,
                                    Map<Integer, Integer> lessonsPerWeek, Map<Integer, Map<Course, Integer>> courseWeeklyCount,
                                    Map<Integer, Map<Course, Integer>> courseTwoWeekCount, 
                                    Map<LocalDate, Set<LocalTime>> groupScheduleOnDate, int numberOfStudents) {
        LocalDate currentDate = startDate;
        int courseLimitPerWeek = getCourseLimitPerWeek(course);
        int courseLimitPerTwoWeeks = getCourseLimitPerTwoWeeks(course);
        int courseLessonsScheduled = 0;
    
        while (currentDate.isBefore(endDate) && courseLessonsScheduled < course.getNumberOfLessons()) {
            if (skipDates.contains(currentDate) || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                currentDate = currentDate.plusDays(1);
                continue;
            }
    
            int weekNumber = currentDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int twoWeekNumber = weekNumber / 2;
            updateLessonCounts(course, weekNumber, twoWeekNumber, lessonsPerWeek, courseWeeklyCount, courseTwoWeekCount);
            Map<Course, Integer> currentCourseWeeklyCountMap = courseWeeklyCount.get(weekNumber);
            Map<Course, Integer> currentCourseTwoWeekCountMap = courseTwoWeekCount.get(twoWeekNumber);
            int currentLessonsPerWeek = lessonsPerWeek.getOrDefault(weekNumber, 0);
            int currentCourseWeeklyCount = currentCourseWeeklyCountMap.getOrDefault(course, 0);
            int currentCourseTwoWeekCount = currentCourseTwoWeekCountMap.getOrDefault(course, 0);
    
            if (currentLessonsPerWeek < MAX_LESSONS_PER_WEEK && currentCourseWeeklyCount < courseLimitPerWeek
            && currentCourseTwoWeekCount < courseLimitPerTwoWeeks) {
                for (LocalTime lessonStartTime : predefinedTimes) {
                    Set<LocalTime> scheduledTimes = groupScheduleOnDate.computeIfAbsent(currentDate, k -> new HashSet<>());
    
                    if (scheduledTimes.contains(lessonStartTime)) {
                        continue;
                    }

                    int currentLessonsPerDate = lessonsPerDate.getOrDefault(currentDate, 0);
    
                    if (currentLessonsPerDate < MAX_LESSONS_PER_DATE) {
                        Teacher teacher = selectTeacher(course, courseLessonsScheduled);
                        LocalDateTime lessonDateTime = LocalDateTime.of(currentDate, lessonStartTime);
    
                        if (!scheduledTimes.contains(lessonStartTime) && isTeacherAvailable(teacher, lessonDateTime, scheduledLessons)) {
                            Classroom classroom = findAvailableClassroom(teacher, group1, course, classrooms, currentDate, lessonStartTime, numberOfStudents);
    
                            if (classroom != null) {
                                Schedule lessonForGroup1 = new Schedule(group1, course, teacher, classroom, currentDate, lessonStartTime);
                                Schedule lessonForGroup2 = new Schedule(group2, course, teacher, classroom, currentDate, lessonStartTime);
                                scheduledLessons.add(lessonForGroup1);
                                scheduledLessons.add(lessonForGroup2);
                                updateTeacherAvailability(teacher, lessonDateTime, teacherAvailability);
                                lessonsPerDate.merge(currentDate, 1, Integer::sum);
                                lessonsPerWeek.merge(weekNumber, 1, Integer::sum);
                                currentCourseWeeklyCountMap.merge(course, 1, Integer::sum);
                                currentCourseTwoWeekCountMap.merge(course, 1, Integer::sum);
                                courseLessonsScheduled++;
                                scheduledTimes.add(lessonStartTime);
    
                                if (shouldBreakScheduling(courseLessonsScheduled, course, currentDate, weekNumber, currentCourseWeeklyCountMap, 
                                currentCourseTwoWeekCountMap, lessonsPerDate, lessonsPerWeek)) {
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

    private List<Schedule> scheduleForGroup(Group group, List<Course> courses, List<Classroom> classrooms,
                                        Map<Teacher, LocalDateTime> teacherAvailability, Map<Classroom, LocalDateTime> classroomAvailability,
                                        LocalDate startDate, LocalDate endDate, Set<LocalDate> skipDates, Map<LocalDate, Set<LocalTime>> occupiedTimes) {
        Map<LocalDate, Integer> lessonsPerDate = lessonsPerDateMap.computeIfAbsent(group, k -> new HashMap<>());
        Map<Integer, Integer> lessonsPerWeek = lessonsPerWeekMap.computeIfAbsent(group, k -> new HashMap<>());
        Map<Integer, Map<Course, Integer>> courseWeeklyCount = new HashMap<>();
        Map<Integer, Map<Course, Integer>> courseTwoWeekCount = new HashMap<>();
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

        twoTeacherCourses.sort(Comparator.comparingInt(Course::getNumberOfLessons).reversed());
        oneTeacherCourses.sort(Comparator.comparingInt(Course::getNumberOfLessons).reversed());

        for (Course course : twoTeacherCourses) {
            if (canScheduleCourse(group, course)) {
                scheduleCourseForGroup(group, course, classrooms, teacherAvailability, 
                                    classroomAvailability, startDate, endDate, 
                                    skipDates, scheduledLessons, predefinedTimes, 
                                    lessonsPerDate, lessonsPerWeek, courseWeeklyCount, 
                                    courseTwoWeekCount, groupScheduleOnDate, occupiedTimes);
            }
        }
    
        for (Course course : oneTeacherCourses) {
            if (canScheduleCourse(group, course)) {
                scheduleCourseForGroup(group, course, classrooms, teacherAvailability, 
                                    classroomAvailability, startDate, endDate, 
                                    skipDates, scheduledLessons, predefinedTimes, 
                                    lessonsPerDate, lessonsPerWeek, courseWeeklyCount, 
                                    courseTwoWeekCount, groupScheduleOnDate, occupiedTimes);
            }
        }
        return scheduledLessons;
    }
    
    private void scheduleCourseForGroup(Group group, Course course, List<Classroom> classrooms,
                                    Map<Teacher, LocalDateTime> teacherAvailability, Map<Classroom, LocalDateTime> classroomAvailability,
                                    LocalDate startDate, LocalDate endDate, Set<LocalDate> skipDates, List<Schedule> scheduledLessons,
                                    List<LocalTime> predefinedTimes, Map<LocalDate, Integer> lessonsPerDate,
                                    Map<Integer, Integer> lessonsPerWeek, Map<Integer, Map<Course, Integer>> courseWeeklyCount,
                                    Map<Integer, Map<Course, Integer>> courseTwoWeekCount, Map<LocalDate, Set<LocalTime>> groupScheduleOnDate, 
                                    Map<LocalDate, Set<LocalTime>> occupiedTimes) {
        LocalDate currentDate = startDate;
        int courseLimitPerWeek = getCourseLimitPerWeek(course);
        int courseLimitPerTwoWeeks = getCourseLimitPerTwoWeeks(course);
        int courseLessonsScheduled = 0;

        if (occupiedTimes == null) {
            occupiedTimes = new HashMap<>();
        }

        while (currentDate.isBefore(endDate) && courseLessonsScheduled < course.getNumberOfLessons()) {
            if (skipDates.contains(currentDate) || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                currentDate = currentDate.plusDays(1);
                continue;
            }

            int weekNumber = currentDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int twoWeekNumber = weekNumber / 2;
            updateLessonCounts(course, weekNumber, twoWeekNumber, lessonsPerWeek, courseWeeklyCount, courseTwoWeekCount);
            Map<Course, Integer> currentCourseWeeklyCountMap = courseWeeklyCount.get(weekNumber);
            Map<Course, Integer> currentCourseTwoWeekCountMap = courseTwoWeekCount.get(twoWeekNumber);
            int currentLessonsPerWeek = lessonsPerWeek.getOrDefault(weekNumber, 0);
            int currentCourseWeeklyCount = currentCourseWeeklyCountMap.getOrDefault(course, 0);
            int currentCourseTwoWeekCount = currentCourseTwoWeekCountMap.getOrDefault(course, 0);

            if (currentLessonsPerWeek < MAX_LESSONS_PER_WEEK && currentCourseWeeklyCount < courseLimitPerWeek 
            && currentCourseTwoWeekCount < courseLimitPerTwoWeeks) {
                for (LocalTime lessonStartTime : predefinedTimes) {
                    Set<LocalTime> scheduledTimes = groupScheduleOnDate.computeIfAbsent(currentDate, k -> new HashSet<>());
                    Set<LocalTime> currentOccupiedTimes = occupiedTimes.computeIfAbsent(currentDate, k -> new HashSet<>());

                    if (scheduledTimes.contains(lessonStartTime) || currentOccupiedTimes.contains(lessonStartTime)) {
                        continue; 
                    }

                    int currentLessonsPerDate = lessonsPerDate.getOrDefault(currentDate, 0);

                    if (currentLessonsPerDate < MAX_LESSONS_PER_DATE) {
                        Teacher teacher = selectTeacher(course, courseLessonsScheduled);
                        LocalDateTime lessonDateTime = LocalDateTime.of(currentDate, lessonStartTime);

                        if (!scheduledTimes.contains(lessonStartTime) && isTeacherAvailable(teacher, lessonDateTime, scheduledLessons)) {
                            Classroom classroom = findAvailableClassroom(teacher, group, course, classrooms, currentDate, lessonStartTime);
                            
                            if (classroom != null && !occupiedTimes.getOrDefault(currentDate, new HashSet<>()).contains(lessonStartTime)) {
                                Schedule lesson = new Schedule(group, course, teacher, classroom, currentDate, lessonStartTime); 
                                scheduledLessons.add(lesson);
                                updateTeacherAvailability(teacher, lessonDateTime, teacherAvailability);
                                lessonsPerDate.merge(currentDate, 1, Integer::sum);
                                lessonsPerWeek.merge(weekNumber, 1, Integer::sum);
                                currentCourseWeeklyCountMap.merge(course, 1, Integer::sum);
                                currentCourseTwoWeekCountMap.merge(course, 1, Integer::sum);
                                courseLessonsScheduled++;
                                scheduledTimes.add(lessonStartTime);
                                currentOccupiedTimes.add(lessonStartTime); 
                                
                                if (shouldBreakScheduling(courseLessonsScheduled, course, currentDate, weekNumber, currentCourseWeeklyCountMap, 
                                currentCourseTwoWeekCountMap, lessonsPerDate, lessonsPerWeek)) {
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
        if (totalLessons <= 32) {
            return 2;
        } else if (totalLessons > 32 && totalLessons <= 48) {
            return 3;
        } else if (totalLessons > 48 && totalLessons <= 64) {
            return 4;
        } else if (totalLessons > 64 && totalLessons <= 72) {
            return 5;
        } else {
            return 8;
        }
    }

    private int getCourseLimitPerTwoWeeks(Course course) {
        int totalLessons = course.getNumberOfLessons();
        if (totalLessons <= 16) {
            return 2;
        } else if (totalLessons > 16 && totalLessons <= 24) {
            return 3;
        } else if (totalLessons > 24 && totalLessons <= 32) {
            return 4;
        } else if (totalLessons > 32 && totalLessons <= 48) {
            return 6;
        } else if (totalLessons > 48 && totalLessons <= 64) {
            return 8;
        } else if (totalLessons > 64 && totalLessons <= 100) {
            return 10;     
        } else {
            return 16;
        }
    }

    private Teacher selectTeacher(Course course, int scheduledLessonsCount) {
        if (course.hasTwoTeachers()) {
            int phaseLength = getCourseLimitPerWeek(course);
            int phaseIndex = (scheduledLessonsCount / phaseLength) % 2;
            return (phaseIndex == 0) ? course.getTeacher1() : course.getTeacher2();
        }
        return course.getTeacher1();
    }

    private boolean isTeacherAvailable(Teacher teacher, LocalDateTime lessonDateTime, List<Schedule> scheduledLessons) {
        List<TeacherUnavailability> unavailabilityList = teacherUnavailabilityRepo
                .findByTeacherAndDate(teacher, lessonDateTime.toLocalDate());

        boolean isUnavailable = unavailabilityList.stream().anyMatch(unavailability ->
                (unavailability.getStartTime().equals(predefinedTimes.get(0)) && unavailability.getEndTime().equals(predefinedTimes.get(predefinedTimes.size()-1)))
                || (!lessonDateTime.toLocalTime().isBefore(unavailability.getStartTime()) && 
                    lessonDateTime.toLocalTime().isBefore(unavailability.getEndTime()))
        );

        return !isUnavailable && scheduledLessons.stream().noneMatch(lesson ->
                lesson.getTeacher().equals(teacher) &&
                lesson.getLessonDateTime().toLocalDate().equals(lessonDateTime.toLocalDate()) &&
                lesson.getLessonDateTime().toLocalTime().equals(lessonDateTime.toLocalTime())
        );
    }
    
    private void updateTeacherAvailability(Teacher teacher, LocalDateTime lessonDateTime, 
                                           Map<Teacher, LocalDateTime> teacherAvailability) {
        teacherAvailability.put(teacher, lessonDateTime);
    }

    private Classroom findAvailableClassroom(Teacher teacher, Group group, Course course, List<Classroom> classrooms, LocalDate date, LocalTime time, int numberOfStudents) {
        for (Classroom classroom : classrooms) {
            if (teacher.getUnteachableClassrooms().contains(classroom)) {
                continue;
            }
            if (classroom.getNumberOfSeats() < numberOfStudents) {
                continue;
            }
            if (teacher.getOnlyOnline() && "Attālināti / Online".equals(classroom.getTitle())) {
                return classroom;
            }
            if (!teacher.getOnlyOnline()) {
                if (isClassroomAvailable(classroom, teacher, course, date, time)) {
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

    private boolean canScheduleCourse(Group group, Course course) {
        return !scheduledCoursesPerGroup.containsKey(group) || !scheduledCoursesPerGroup.get(group).contains(course);
    }

    private void recordOccupiedTimes(List<Schedule> schedules, Map<LocalDate, Set<LocalTime>> occupiedTimes) {
        for (Schedule schedule : schedules) {
            LocalDate date = schedule.getDate();
            LocalTime time = schedule.getTime();
            occupiedTimes.putIfAbsent(date, new HashSet<>());
            occupiedTimes.get(date).add(time);
        }
    }

    private void updateLessonCounts(Course course, int weekNumber, int twoWeekNumber, Map<Integer, Integer> lessonsPerWeek,
                                Map<Integer, Map<Course, Integer>> courseWeeklyCount, Map<Integer, Map<Course, Integer>> courseTwoWeekCount) {
        lessonsPerWeek.putIfAbsent(weekNumber, 0);
        courseWeeklyCount.putIfAbsent(weekNumber, new HashMap<>());
        courseTwoWeekCount.putIfAbsent(twoWeekNumber, new HashMap<>());
        Map<Course, Integer> currentCourseWeeklyCountMap = courseWeeklyCount.get(weekNumber);
        Map<Course, Integer> currentCourseTwoWeekCountMap = courseTwoWeekCount.get(twoWeekNumber);
        currentCourseWeeklyCountMap.putIfAbsent(course, 0);
        currentCourseTwoWeekCountMap.putIfAbsent(course, 0);
    }

    private boolean shouldBreakScheduling(int courseLessonsScheduled, Course course, LocalDate currentDate, int weekNumber,
                                    Map<Course, Integer> currentCourseWeeklyCountMap, Map<Course, Integer> currentCourseTwoWeekCountMap,
                                    Map<LocalDate, Integer> lessonsPerDate, Map<Integer, Integer> lessonsPerWeek) {
        return courseLessonsScheduled >= course.getNumberOfLessons() ||
               lessonsPerDate.get(currentDate) >= MAX_LESSONS_PER_DATE || 
               lessonsPerWeek.get(weekNumber) >= MAX_LESSONS_PER_WEEK ||
               currentCourseWeeklyCountMap.get(course) >= getCourseLimitPerWeek(course) || 
               currentCourseTwoWeekCountMap.get(course) >= getCourseLimitPerTwoWeeks(course);
    }

}