import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Stair {
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
