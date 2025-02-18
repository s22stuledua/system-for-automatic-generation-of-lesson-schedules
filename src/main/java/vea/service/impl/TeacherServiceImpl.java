package vea.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vea.model.Teacher;
import vea.repo.TeacherRepo;
import vea.service.TeacherService;

@Service
public class TeacherServiceImpl implements TeacherService {
    
    @Autowired
	private TeacherRepo teacherRepo;

	@Override
	public List<Teacher> findAllTeachers() {
		return teacherRepo.findAll();
	}

	@Override
	public List<Teacher> searchTeacherByName(String keyword) {
		if (keyword != null) {
			return teacherRepo.search(keyword);
		}
		return teacherRepo.findAll();
	}

	@Override
	public List<Teacher> getSortedTeachers(boolean ascending) {
		if (ascending) {
            return teacherRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
        } else {
            return teacherRepo.findAll();
        }
    }

	@Override
	public Teacher findTeacherById(Long id) throws Exception {
		return teacherRepo.findById(id).orElseThrow(() -> new Exception("Pasniedzējs ar šo ID netika atrasts"));
	}

	@Override
	public void createTeacher(Teacher teacher) {
		teacherRepo.save(teacher);
	}

	@Override
	public void updateTeacher(Teacher teacher) {
		teacherRepo.save(teacher);
	}

	@Override
	public void deleteTeacher(Long id) throws Exception {
		Teacher teacher = teacherRepo.findById(id).orElseThrow(() -> new Exception("Pasniedzējs ar šo ID netika atrasts"));
		teacherRepo.deleteById(teacher.getId());
	}

}