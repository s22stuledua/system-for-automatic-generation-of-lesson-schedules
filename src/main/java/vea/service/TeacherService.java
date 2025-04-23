package vea.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import vea.model.Teacher;
import vea.model.TeacherUnavailability;

public interface TeacherService {
    
    public List<Teacher> findAllTeachers();

	public List<Teacher> searchTeacherByName(String keyword);

	public List<Teacher> getSortedTeachers(boolean ascending);

	public Teacher findTeacherById(Long id) throws Exception;

	public void addUnavailability(Teacher teacher, LocalDate date, LocalTime startTime, LocalTime endTime);

	public List<TeacherUnavailability> getUnavailabilitiesForTeacher(Teacher teacher);

	public void deleteUnavailability(Long teacherId, Long unavailabilityId);

	public void createTeacher(Teacher teacher);

	public void deleteTeacher(Long id) throws Exception;

}