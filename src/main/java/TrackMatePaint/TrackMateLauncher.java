package TrackMatePaint;

import java.util.Map;
import java.util.HashMap;

import fiji.plugin.trackmate.*;
import ij.ImagePlus;

import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;
import loci.formats.FormatException;

import fiji.plugin.trackmate.features.FeatureFilter;
import fiji.plugin.trackmate.tracking.jaqaman.SparseLAPTrackerFactory;
import fiji.plugin.trackmate.detection.LogDetectorFactory;
import fiji.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;
import fiji.plugin.trackmate.gui.displaysettings.DisplaySettings;


import java.io.IOException;

public class TrackMateLauncher {

    public TrackMateLauncher() {

    }

    public static void main(String[] args) throws IOException, FormatException {
        // Optional: start the Fiji GUI
        //new ImageJ();  // launches ImageJ main window

        // Path to your ND2 file
        String path = "/Users/hans/Downloads/221012 Images/221012-Exp-3-A4-2.nd2";

        // Load images using Bio-Formats
        ImporterOptions options = new ImporterOptions();
        options.setId(path);
        options.setSplitChannels(false);
        options.setSplitTimepoints(false);
        options.setSplitFocalPlanes(false);
        ImagePlus[] imps = BF.openImagePlus(options);

        if (imps.length == 0) {
            System.err.println("No image found.");
            return;
        }
        ImagePlus imp = imps[0];
        imp.show();

        // Setup model and settings
        Model model = new Model();
        // SelectionModel selectionModel = new SelectionModel(model);
        model.setLogger(Logger.IJ_LOGGER);

        // Create DisplaySettings (default settings for your ImagePlus):
        DisplaySettings displaySettings = DisplaySettings.defaultStyle();

        Settings settings = new Settings(imp);

        // === DETECTOR ===
        settings.detectorFactory = new LogDetectorFactory();

        Map<String, Object> detectorSettings = new HashMap<>();
        detectorSettings.put("TARGET_CHANNEL", 1);               // your value
        detectorSettings.put("RADIUS", 0.5);                     // your value
        detectorSettings.put("DO_SUBPIXEL_LOCALIZATION", false); // your value
        detectorSettings.put("THRESHOLD", 20.0);                  // your value
        detectorSettings.put("DO_MEDIAN_FILTERING", false);      // your value
        settings.detectorSettings = detectorSettings;

        // === SPOT FILTER ===
        FeatureFilter spotFilter = new FeatureFilter(Spot.QUALITY, 0.0, true);
        settings.addSpotFilter(spotFilter);

        // === TRACKER ===
        settings.trackerFactory = new SparseLAPTrackerFactory();

        Map<String, Object> trackerSettings = settings.trackerFactory.getDefaultSettings();
        trackerSettings.put("LINKING_MAX_DISTANCE", 0.6);
        trackerSettings.put("ALTERNATIVE_LINKING_COST_FACTOR", 1.05);

        trackerSettings.put("ALLOW_GAP_CLOSING", true);
        trackerSettings.put("GAP_CLOSING_MAX_DISTANCE", 1.2);
        trackerSettings.put("MAX_FRAME_GAP", 3);  // example

        trackerSettings.put("ALLOW_TRACK_SPLITTING", false);
        trackerSettings.put("SPLITTING_MAX_DISTANCE", 15.0);

        trackerSettings.put("ALLOW_TRACK_MERGING", false);
        trackerSettings.put("MERGING_MAX_DISTANCE", 15.0);

        settings.trackerSettings = trackerSettings;

        // === ANALYZERS ===
        settings.addAllAnalyzers();

        // === TRACK FILTER ===
        FeatureFilter trackFilter = new FeatureFilter("NUMBER_SPOTS", 3.0, true);
        settings.addTrackFilter(trackFilter);

        // === INSTANTIATE TrackMate ===
        TrackMate trackmate = new TrackMate(model, settings);
        model.setLogger(Logger.IJ_LOGGER);

        if (!trackmate.checkInput()) {
            System.err.println("TrackMate error 1: " + trackmate.getErrorMessage());
            return;
        }
        else {
            System.err.println("Survived TrackMate checkInput");
        }
        if (!trackmate.execDetection())
        {
            System.err.println("TrackMate error 2: " + trackmate.getErrorMessage());
            return;
        }
        else {
            System.err.println("Survived TrackMate execDetection");
        }

        int nrSpots = model.getSpots().getNSpots(false);
        if (nrSpots > 1_000_000) {
            System.err.println("Too many spots");
        }
        else {
            System.err.println("Nr Spots found: " + nrSpots);
        }

        if (!trackmate.process()) {
            System.err.println("TrackMate error 4: " + trackmate.getErrorMessage());
            return;
        }
        int tracks = model.getTrackModel().nTracks(false);
        System.out.println("Nr Tracks found: " + tracks);

        TrackModel trackModel = model.getTrackModel();
        FeatureModel featureModel = model.getFeatureModel();
        SelectionModel selectionModel = new SelectionModel(model);

        // int tracks = trackModel.nTracks(false);  // Get all tracks
        int filteredTracks = trackModel.nTracks(true);  // Get filtered tracks

        System.out.println("Nr Spots found: " + nrSpots);
        System.out.println("Nr Tracks found: " + tracks);
        System.out.println("Nr Filtered Tracks found: " + filteredTracks);

        // Read the default display settings.
        displaySettings.setSpotVisible(false);
        displaySettings.setTrackColorBy(DisplaySettings.TrackMateObject.TRACKS, "TRACK_DURATION");

        HyperStackDisplayer hyperStackDisplayer = new HyperStackDisplayer(model, selectionModel, imp, displaySettings);
        hyperStackDisplayer.render();
        hyperStackDisplayer.refresh();

        // Optionally: Display results
        //HyperStackDisplayer displayer = new HyperStackDisplayer(model, selectionModel, displaySettings);
        //displayer.render();
        //displayer.refresh();

        System.out.println("Done");
    }

}
