package vea.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vea.model.Classroom;
import vea.repo.ClassroomRepo;
import vea.service.ClassroomService;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    
    @Autowired
	private ClassroomRepo classroomRepo;

	@Override
	public List<Classroom> findAllClassrooms() {
		return classroomRepo.findAll();
	}

	@Override
	public List<Classroom> searchClassroomByTitle(String keyword) {
		if (keyword != null) {
			return classroomRepo.search(keyword);
		}
		return classroomRepo.findAll();
	}

	@Override
	public Classroom findClassroomById(Long id) throws Exception {
		return classroomRepo.findById(id).orElseThrow(() -> new Exception("Auditorija ar šo ID nav atrasta"));
	}

	@Override
	public void createClassroom(Classroom classroom) {
		classroomRepo.save(classroom);
	}

	@Override
	public void deleteClassroom(Long id) throws Exception {
		Classroom classroom = classroomRepo.findById(id).orElseThrow(() -> new Exception("Auditorija ar šo ID nav atrasta"));
		classroomRepo.deleteById(classroom.getId());
	}

}