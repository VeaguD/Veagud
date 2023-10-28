package com.veagud.model.winderSteps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс, представляющий собой данные о забежных ступенях.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WinderStepsData {
    /**
     * Первая точка забежной ступени.
     */
    private Dot dot1;

    /**
     * Вторая точка забежной ступени.
     */
    private Dot dot2;

    /**
     * Третья точка забежной ступени.
     */
    private Dot dot3;

    /**
     * Четвертая точка забежной ступени.
     */
    private Dot dot4;

    /**
     * Высота базового уровня забежной ступени.
     */
    private double baseElevation;

    /**
     * Флаг, указывающий, имеет ли забежная ступень четыре точки.
     */
    private boolean isFourDots;

}