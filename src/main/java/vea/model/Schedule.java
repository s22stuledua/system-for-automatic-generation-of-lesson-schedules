package vea.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "schedule")
public class Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idschedule")
    private Long id;
    
    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @ManyToOne private Group group;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @ManyToOne private Course course;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @ManyToOne private Teacher teacher;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @ManyToOne private Classroom classroom;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private LocalTime time;

    public Schedule(Group group, Course  course, Teacher teacher, Classroom classroom, LocalDate date, LocalTime time) {
        this.group = group;
        this.course = course;
        this.teacher = teacher;
        this.classroom = classroom;
        this.date = date;
        this.time = time;
    }

    public LocalDateTime getLessonDateTime() {
        return LocalDateTime.of(date, time);
    }

}