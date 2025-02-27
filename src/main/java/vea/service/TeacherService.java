package vea.service;

import java.util.List;
import vea.model.Teacher;

public interface TeacherService {
    
    public List<Teacher> findAllTeachers();

	public List<Teacher> searchTeacherByName(String keyword);

	public List<Teacher> getSortedTeachers(boolean ascending);

	public Teacher findTeacherById(Long id) throws Exception;

	public void createTeacher(Teacher teacher);

	public void deleteTeacher(Long id) throws Exception;

}