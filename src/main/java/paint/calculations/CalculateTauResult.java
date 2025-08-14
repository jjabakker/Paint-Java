package paint.calculations;

public class CalculateTauResult {
    private  double tau;
    private  double rsquared;
    private  Status status;

    public enum Status {
        TAU_SUCCESS,
        TAU_INSUFFICIENT_POINTS,
        TAU_RSQUARED_TOO_LOW,
        TAU_NO_FIT
    }

    public CalculateTauResult(double tau, double rsquared, Status status) {
        this.tau = tau;
        this.rsquared = rsquared;
        this.status = status;
    }

    public double getTau() { return tau; }
    public double getRSquared() { return rsquared; }
    public Status getStatus() { return status; }
}