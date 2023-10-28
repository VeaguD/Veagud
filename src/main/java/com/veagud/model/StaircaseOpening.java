package com.veagud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "staircase_opening")
public class StaircaseOpening {

    /** Идентификатор проема. */
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /** Ширина проема. */
    private int width;

    /** Длина проема. */
    private int length;

    /** Высота проема. */
    private int height;

    /** Наличие опорных ног. */
    private boolean hasSupportLegs = true;

    /** Толщина чистового пола. */
    private int finishedFloorThickness = 20;

    /** Ширина марша */ // планируется упразднить и писать зазор межмаршевого пространства
    @Deprecated
    private int flightWidth = 1000;

    /** Отступ. */
    private int indent = 20;

    /** Теоретическая ширина площадки. */
    private int theoreticalPlatformWidth = 900;

    /** Минимальная высота ступени. */
    private double minStepHeight = 120;

    /** Максимальная высота ступени. */
    private double maxStepHeight = 200;

    /** Минимальная глубина ступени. */
    private int minStepDepth = 250;

    /** Максимальная глубина ступени. */
    private int maxStepDepth = 310;

    /** Ограничение расстояния от стены до лестницы. */
    private int distanceFromWallToStair = 3000;

    /** Флаг направления лестницы: True - подъем справа, False - слева. */
    private boolean isDirectionRight = true;
}
