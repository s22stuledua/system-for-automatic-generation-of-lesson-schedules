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
@Table(name = "classroom")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idclassroom")
    private Long id;

    @Size(min = 1, max = 30, message = "Šis lauks nedrīkst būt tukšs")
    @Column(name = "title", nullable = false, unique = true, length = 30)
    private String title;

    @NotNull(message = "Šis lauks nedrīkst būt tukšs")
    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

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
    
}    