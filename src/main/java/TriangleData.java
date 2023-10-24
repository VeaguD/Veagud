public class TriangleData {
    double x1, y1, x2, y2, x3, y3, x4, y4;
    double baseElevation;
    boolean isFourPoints;

    public TriangleData(double x1, double y1, double x2, double y2, double x3, double y3, double baseElevation) {
        this(x1, y1, x2, y2, x3, y3, 0, 0, baseElevation, false);
    }

    public TriangleData(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double baseElevation) {
        this(x1, y1, x2, y2, x3, y3, x4, y4, baseElevation, true);
    }

    private TriangleData(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double baseElevation, boolean isFourPoints) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.x4 = x4;
        this.y4 = y4;
        this.baseElevation = baseElevation;
        this.isFourPoints = isFourPoints;
    }
}
