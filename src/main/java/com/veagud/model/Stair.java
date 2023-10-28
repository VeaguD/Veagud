package com.veagud.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "stair")
public class Stair {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    /**
     * Площадка лестницы
     */
    private int platform;
    /**
     * Глубина ступени в миллиметрах
     */
    private int StepDepth;
    /**
     * Отступ от стен
     */
    private int indent;
    /**
     * Количество ступеней в нижнем марше
     */
    private int lowerStairsCount;
    /**
     * Количество ступеней в верхнем марше
     */
    private int upperStairsCount;
    /**
     * Высота ступени в миллиметрах
     */
    private double stepHeight;
    /**
     * Наличие опорных ног у лестницы.
     * True - если опорные ноги есть, False - если нет.
     */
    private boolean supportLegs = true;
    /**
     * Ширина марша лестницы в миллиметрах
     */
    private int flightWidth = 1000;
    /**
     * Расстояние между маршами в миллиметрах
     */
    private int betweenFlights = 100;
    /**
     * Количество забежных ступеней
     */
    private int winderStepsCount;
    /**
     * Направление подъема.
     * True - если подъем справа, False - если слева.
     */
    private boolean isDirectionRight = true;

}