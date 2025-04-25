package vea.model;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "teacher_unavailability")
public class TeacherUnavailability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idunavailability")
    private Long id;

    @JoinColumn(name = "teacher")
    @ManyToOne private Teacher teacher;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "startTime")
    private LocalTime startTime;
    
    @Column(name = "endTime")
    private LocalTime endTime;

    public TeacherUnavailability(Teacher teacher, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.teacher = teacher;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}