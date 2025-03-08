package vea.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	
}