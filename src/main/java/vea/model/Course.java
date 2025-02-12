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

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "number_of_lessons", nullable = false)
    private Integer numberOfLessons;

    @ManyToOne
    @JoinColumn(name = "teacher1", nullable = false)
    private Teacher teacher1;

    @ManyToOne
    @JoinColumn(name = "teacher2")
    private Teacher teacher2;

    @Column(name = "equipment1")
	@Enumerated(EnumType.STRING)
	private Equipment equipment1;

    @Column(name = "equipment2")
	@Enumerated(EnumType.STRING)
	private Equipment equipment2;

    @Column(name = "equipment3")
	@Enumerated(EnumType.STRING)
	private Equipment equipment3;

    @Column(name = "equipment4")
	@Enumerated(EnumType.STRING)
	private Equipment equipment4;

    @Column(name = "equipment5")
	@Enumerated(EnumType.STRING)
	private Equipment equipment5;

    @Column(name = "equipment6")
	@Enumerated(EnumType.STRING)
	private Equipment equipment6;

    @Column(name = "equipment7")
	@Enumerated(EnumType.STRING)
	private Equipment equipment7;

    @Column(name = "equipment8")
	@Enumerated(EnumType.STRING)
	private Equipment equipment8;

}