package com.veagud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "proem")
public class Proem {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    private int width;
    private int length;
    private int height;

    boolean hasNogi = true;
    int chistovoiPol = 20;
    //упразднить, дававать зазор межмаршевого пространства
    int shirinamarsha = 1000;
    int otstup = 20;
    //изначально предполагаемая площадка
    int ploshadkaShirinaTeor = 900;

    //Диапозон высот ступеней
    double minVValue = 120;
    double maxVValue = 200;
    // Диапазон для глубины Ступени
    int minT = 250;
    int maxT = 310;

    //ограничение расстояния (точка начала лестницы от дальней стены)
    int lengthOtStenDoCraya = 3000;

    boolean isRightDirection = true;
}
