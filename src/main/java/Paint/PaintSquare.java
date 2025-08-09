package Paint;

public class PaintSquare {
    private String uniqueKey;
    private int recordingSequenceNr;
    private int conditionNumber;
    private int replicateNumber;
    private int squareNumber;
    private int rowNumber;
    private int colNumber;
    private int labelNumber;
    private int cellId;
    private int numberSpots;
    private int numberTracks;
    private double X0;
    private double Y0;
    private double X1;
    private double Y1;
    private boolean selected;
    private double variability;
    private double density;
    private double densityRatio;
    private double tau;
    private double rSquared;
    private double medianDiffusionCoefficient;
    private double meanDiffusionCoefficient;
    private double medianDiffusionCoefficientExt;
    private double meanDiffusionCoefficientExt;
    private double medianLongTrackDuration;
    private double medianShortTrackDuration;
    private double medianDisplacement;
    private double maxDisplacement;
    private double totalDisplacement;
    private double medianMaxSpeed;
    private double maxMaxSpeed;
    private double medianMeanSpeed;
    private double maxMeanSpeed;
    private double maxTrackDuration;
    private double totalTrackDuration;
    private double medianTrackDuration;
    private boolean squareManuallyExcluded;
    private boolean imageExcluded;

    // Constructor
    public PaintSquare(String uniqueKey,
                       int recordingSequenceNr,
                       int conditionNumber,
                       int replicateNumber,
                       int squareNumber,
                       int rowNumber,
                       int colNumber,
                       int labelNumber,
                       int cellId,
                       int numberSpots,
                       int numberTracks,
                       double X0,
                       double Y0,
                       double X1,
                       double Y1,
                       boolean selected,
                       double variability,
                       double density,
                       double densityRatio,
                       double tau,
                       double rSquared,
                       double medianDiffusionCoefficient,
                       double meanDiffusionCoefficient,
                       double medianDiffusionCoefficientExt,
                       double meanDiffusionCoefficientExt,
                       double medianLongTrackDuration,
                       double medianShortTrackDuration,
                       double medianDisplacement,
                       double maxDisplacement,
                       double totalDisplacement,
                       double medianMaxSpeed,
                       double maxMaxSpeed,
                       double medianMeanSpeed,
                       double maxMeanSpeed,
                       double maxTrackDuration,
                       double totalTrackDuration,
                       double medianTrackDuration,
                       boolean squareManuallyExcluded,
                       boolean imageExcluded) {
        this.uniqueKey = uniqueKey;
        this.recordingSequenceNr = recordingSequenceNr;
        this.conditionNumber = conditionNumber;
        this.replicateNumber = replicateNumber;
        this.squareNumber = squareNumber;
        this.rowNumber = rowNumber;
        this.colNumber = colNumber;
        this.labelNumber = labelNumber;
        this.cellId = cellId;
        this.numberSpots = numberSpots;
        this.numberTracks = numberTracks;
        this.X0 = X0;
        this.Y0 = Y0;
        this.X1 = X1;
        this.Y1 = Y1;
        this.selected = selected;
        this.variability = variability;
        this.density = density;
        this.densityRatio = densityRatio;
        this.tau = tau;
        this.rSquared = rSquared;
        this.medianDiffusionCoefficient = medianDiffusionCoefficient;
        this.meanDiffusionCoefficient = meanDiffusionCoefficient;
        this.medianDiffusionCoefficientExt = medianDiffusionCoefficientExt;
        this.meanDiffusionCoefficientExt = meanDiffusionCoefficientExt;
        this.medianLongTrackDuration = medianLongTrackDuration;
        this.medianShortTrackDuration = medianShortTrackDuration;
        this.medianDisplacement = medianDisplacement;
        this.maxDisplacement = maxDisplacement;
        this.totalDisplacement = totalDisplacement;
        this.medianMaxSpeed = medianMaxSpeed;
        this.maxMaxSpeed = maxMaxSpeed;
        this.medianMeanSpeed = medianMeanSpeed;
        this.maxMeanSpeed = maxMeanSpeed;
        this.maxTrackDuration = maxTrackDuration;
        this.totalTrackDuration = totalTrackDuration;
        this.medianTrackDuration = medianTrackDuration;
        this.squareManuallyExcluded = squareManuallyExcluded;
        this.imageExcluded = imageExcluded;
    }

    // getters and setters here ...
}