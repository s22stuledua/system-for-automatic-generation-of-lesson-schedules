package vea.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcourse")
    private Long id;

    @Size(min = 1, max = 150, message = "Šis lauks nedrīkst būt tukšs")
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @Column(name = "number_of_lessons", nullable = false)
    private Integer numberOfLessons;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @JoinColumn(name = "teacher1", nullable = false)
    @ManyToOne public Teacher teacher1;

    @JoinColumn(name = "teacher2")
    @ManyToOne public Teacher teacher2;

    public boolean hasTwoTeachers() {
        return teacher2 != null;
    }

    @Column(name = "equipment1")
	@Enumerated(EnumType.STRING)
	public Equipment equipment1;

    @Column(name = "equipment2")
	@Enumerated(EnumType.STRING)
	public Equipment equipment2;

    @Column(name = "equipment3")
	@Enumerated(EnumType.STRING)
	public Equipment equipment3;

    @Column(name = "equipment4")
	@Enumerated(EnumType.STRING)
	public Equipment equipment4;

    @Column(name = "equipment5")
	@Enumerated(EnumType.STRING)
	public Equipment equipment5;

    @Column(name = "equipment6")
	@Enumerated(EnumType.STRING)
	public Equipment equipment6;

    @Column(name = "equipment7")
	@Enumerated(EnumType.STRING)
	public Equipment equipment7;

    @Column(name = "equipment8")
	@Enumerated(EnumType.STRING)
	public Equipment equipment8;

}