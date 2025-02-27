package vea.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "holiday")
public class Holiday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idholiday")
	private Long id;

	@Size(min = 1, max = 150, message = "Šis lauks nedrīkst būt tukšs")
	@Column(name = "title", nullable = false, length = 150)
    private String title;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	
}