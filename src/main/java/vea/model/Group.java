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

    @Column(name = "active")
	private boolean active;

}