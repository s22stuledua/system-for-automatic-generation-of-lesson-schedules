package vea.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "course1")
    private Course course1;

    @ManyToOne
    @JoinColumn(name = "course2")
    private Course course2;

    @ManyToOne
    @JoinColumn(name = "course3")
    private Course course3;

    @ManyToOne
    @JoinColumn(name = "course4")
    private Course course4;

    @ManyToOne
    @JoinColumn(name = "course5")
    private Course course5;

    @ManyToOne
    @JoinColumn(name = "course6")
    private Course course6;

    @ManyToOne
    @JoinColumn(name = "course7")
    private Course course7;

    @ManyToOne
    @JoinColumn(name = "course8")
    private Course course8;

    @ManyToOne
    @JoinColumn(name = "course9")
    private Course course9;

    @ManyToOne
    @JoinColumn(name = "course10")
    private Course course10;

    @ManyToOne
    @JoinColumn(name = "course11")
    private Course course11;

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

    @Column(name = "last_semester")
	private Boolean lastSemester;

    @Column(name = "active")
	private Boolean active;

}