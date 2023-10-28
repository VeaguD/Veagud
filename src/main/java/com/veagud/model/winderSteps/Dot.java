package com.veagud.model.winderSteps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий собой точку с координатами X и Y.
 */
@Getter
@Setter
@AllArgsConstructor
public class Dot {
    /**
     * Координата X точки.
     */
    private double x;

    /**
     * Координата Y точки.
     */
    private double y;

}