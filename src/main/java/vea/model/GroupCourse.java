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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "group_course")
public class GroupCourse {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idgroup_course")
	private Long id;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @JoinColumn(name = "group1", nullable = false)
    @ManyToOne private Group group1;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @JoinColumn(name = "group2", nullable = false)
    @ManyToOne private Group group2;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @JoinColumn(name = "course", nullable = false)
    @ManyToOne private Course course;
    
}