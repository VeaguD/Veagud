package com.veagud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table (name = "stair")
public class Stair {

    @Column(name = "id")
    @Id
    Long id;
    int ploshadka;
    int stupenGlubina;
    int otstup;
    int lowerStairsCount;
    int upperStairsCount;
    double heightStupen;
    boolean hasNogi = true;
    int shirinamarsha = 1000;
    int betweenMarsh = 100;

    int countZabStupen;
    boolean isRightDirection = true;
}
