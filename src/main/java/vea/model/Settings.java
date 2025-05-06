package vea.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsettings")
    private Long id;

    @Column(name = "weeksInSemester")
    private int weeksInSemester = 16;

    @Column(name = "maxLessonsPerDateForGroup")
    private int maxLessonsPerDateForGroup = 4;

    @Column(name = "maxLessonsPerWeekForGroup")
    private int maxLessonsPerWeekForGroup = 15;

    @Column(name = "maxLessonsPerDateForTeacher")
    private int maxLessonsPerDateForTeacher = 4;

    @Column(name = "maxLessonsPerWeekForTeacher")
    private int maxLessonsPerWeekForTeacher = 20;

    @Column(name = "weekLimit1")
    private int weekLimit1 = 2;

    @Column(name = "weekLimit2")
    private int weekLimit2 = 3;

    @Column(name = "weekLimit3")
    private int weekLimit3 = 4;

    @Column(name = "weekLimit4")
    private int weekLimit4 = 5;

    @Column(name = "weekLimit5")
    private int weekLimit5 = 8;

    @Column(name = "twoWeekLimit1")
    private int twoWeekLimit1 = 2;

    @Column(name = "twoWeekLimit2")
    private int twoWeekLimit2 = 3;

    @Column(name = "twoWeekLimit3")
    private int twoWeekLimit3 = 4;

    @Column(name = "twoWeekLimit4")
    private int twoWeekLimit4 = 6;

    @Column(name = "twoWeekLimit5")
    private int twoWeekLimit5 = 8;

    @Column(name = "twoWeekLimit6")
    private int twoWeekLimit6 = 10;

    @Column(name = "twoWeekLimit7")
    private int twoWeekLimit7 = 16;

}