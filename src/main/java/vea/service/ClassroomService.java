package vea.service;

import java.util.List;
import vea.model.Classroom;

public interface ClassroomService {
    
    public List<Classroom> findAllClassrooms();

	public List<Classroom> searchClassroomByTitle(String keyword);

	public Classroom findClassroomById(Long id) throws Exception;

	public void createClassroom(Classroom classroom);

	public void deleteClassroom(Long id) throws Exception;

}