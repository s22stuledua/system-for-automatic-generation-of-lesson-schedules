package vea.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
@Table(name = "teacher")
public class Teacher {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idteacher")
	private Long id;

	@Size(min = 1, max = 100, message = "Šis lauks nedrīkst būt tukšs")
	@Column(name = "name", unique = true, nullable = false, length = 100)
	private String name;

	@NotNull(message = "Šis lauks nedrīkst būt tukšs")
	@Column(name = "position", nullable = false)
	@Enumerated(EnumType.STRING)
	private Position position;
	
	@Column(name = "only_online")
	private Boolean onlyOnline;

	@ManyToOne
    @JoinColumn(name = "classroom1")
    private Classroom classroom1;

	@ManyToOne
    @JoinColumn(name = "classroom2")
    private Classroom classroom2;
	
	@ManyToOne
    @JoinColumn(name = "classroom3")
    private Classroom classroom3;

	@ManyToOne
    @JoinColumn(name = "classroom4")
    private Classroom classroom4;

	@ManyToOne
    @JoinColumn(name = "classroom5")
    private Classroom classroom5;

	public List<Classroom> getUnteachableClassrooms() {
        return Stream.of(classroom1, classroom2, classroom3, classroom4, classroom5)
                     .filter(Objects::nonNull).collect(Collectors.toList());
    }

}