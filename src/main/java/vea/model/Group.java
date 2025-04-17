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
@Table(name = "study_group")
public class Group {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idstudy_group")
	private Long id;

    @Size(min = 1, max = 50, message = "Šis lauks nedrīkst būt tukšs")
	@Column(name = "title", nullable = false, unique = true, length = 50)
    private String title;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @Column(name = "number_of_students", nullable = false)
    private Integer numberOfStudents;

    @JoinColumn(name = "course1")
    @ManyToOne private Course course1;
    
    @JoinColumn(name = "course2")
    @ManyToOne private Course course2;

    @JoinColumn(name = "course3")
    @ManyToOne private Course course3;
    
    @JoinColumn(name = "course4")
    @ManyToOne private Course course4;
    
    @JoinColumn(name = "course5")
    @ManyToOne private Course course5;
    
    @JoinColumn(name = "course6")
    @ManyToOne private Course course6;
    
    @JoinColumn(name = "course7")
    @ManyToOne private Course course7;

    @JoinColumn(name = "course8")
    @ManyToOne private Course course8;

    @JoinColumn(name = "course9")
    @ManyToOne private Course course9;

    @JoinColumn(name = "course10")
    @ManyToOne private Course course10;

    @JoinColumn(name = "course11")
    @ManyToOne private Course course11;

    public int calculateTotalLessons() {
        int totalLessons = 0;
        if (course1 != null && course1.getNumberOfLessons() != null) {
            totalLessons += course1.getNumberOfLessons();
        }
        if (course2 != null && course2.getNumberOfLessons() != null) {
            totalLessons += course2.getNumberOfLessons();
        }
        if (course3 != null && course3.getNumberOfLessons() != null) {
            totalLessons += course3.getNumberOfLessons();
        }
        if (course4 != null && course4.getNumberOfLessons() != null) {
            totalLessons += course4.getNumberOfLessons();
        }
        if (course5 != null && course5.getNumberOfLessons() != null) {
            totalLessons += course5.getNumberOfLessons();
        }
        if (course6 != null && course6.getNumberOfLessons() != null) {
            totalLessons += course6.getNumberOfLessons();
        }
        if (course7 != null && course7.getNumberOfLessons() != null) {
            totalLessons += course7.getNumberOfLessons();
        }
        if (course8 != null && course8.getNumberOfLessons() != null) {
            totalLessons += course8.getNumberOfLessons();
        }
        if (course9 != null && course9.getNumberOfLessons() != null) {
            totalLessons += course9.getNumberOfLessons();
        }
        if (course10 != null && course10.getNumberOfLessons() != null) {
            totalLessons += course10.getNumberOfLessons();
        }
        if (course11 != null && course11.getNumberOfLessons() != null) {
            totalLessons += course11.getNumberOfLessons();
        }
        return totalLessons;
    }

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @Column(name = "semester", nullable = false)
	@Enumerated(EnumType.STRING)
	private Semester semester;

    @Column(name = "last_semester")
	private Boolean lastSemester;

}