package org.biobank.platedecoder.dmscanlib;

/**
 * Used to configure the DataMatrix 2D barcode decoder.
 *
 * @author Nelson Loyola
 *
 */
public class DecodeOptions {
   /**
    * The default value for the minimum edge factor.
    *
    * <p>The length, in inches, of the smallest expected edge in image.
    */
   public static final double DEFAULT_MIN_EDGE_FACTOR = 0.15;

   /**
    * The default value for the maximum edge factor.
    *
    * <p>The length, in inches, of largest expected edge in image.
    */
   public static final double DEFAULT_MAX_EDGE_FACTOR = 0.3;

   /**
    * The default value for the scan gap factor.
    *
    * <p>The scan gap defines the gap, in inches, of the between lines in the scan grid used to
    * examine the image.
    */
   public static final double DEFAULT_SCAN_GAP_FACTOR = 0.1;

   /**
    * The default value for square deviation.
    *
    * <p>The maximu deviation, in degrees, from squareness between adjacent barcode sides. This is
    * the recommened value for flat images.
    */
   public static final long DEFAULT_SQUARE_DEV = 10;

   /**
    * The default value for edge threshold.
    *
    * <p>The minimum edge threshold as a percentage of maximum. For example, an edge between a pure
    * white and pure black pixel would have an intensity of 100.
    */
   public static final long DEFAULT_EDGE_THRESH = 5;

   /**
    * The default value for the number of corrections.
    *
    * <p>The number of errors to correct per image.
    */
   public static final long DEFAULT_CORRECTIONS = 10;

   /**
    * The default value for shrink.
    *
    * <p>Internally shrink image by factor of {@code N}. Shrinking is accomplished by skipping N-1
    * pixels at a time, often producing significantly faster scan times.
    *
    * <p>Do not use any other values unless you know what you are doing.
    */
   public static final long DEFAULT_SHRINK = 1;

   private final double minEdgeFactor;
   private final double maxEdgeFactor;
   private final double scanGapFactor;
   private final long squareDev;
   private final long edgeThresh;
   private final long corrections;
   private final long shrink;

   /**
    * Used to configure the DataMatrix 2D barcode decoder.
    *
    * @param minEdgeFactor  See {@link #DEFAULT_MIN_EDGE_FACTOR}.
    *
    * @param maxEdgeFactor  See {@link #DEFAULT_MAX_EDGE_FACTOR}.
    *
    * @param scanGapFactor  See {@link #DEFAULT_SCAN_GAP_FACTOR}.
    *
    * @param squareDev  See {@link #DEFAULT_SQUARE_DEV}.
    *
    * @param edgeThresh  See {@link #DEFAULT_EDGE_THRESH}.
    *
    * @param corrections  See {@link #DEFAULT_CORRECTIONS}.
    *
    * @param shrink See {@link #DEFAULT_SHRINK}.
    */
   public DecodeOptions(double minEdgeFactor,
                        double maxEdgeFactor,
                        double scanGapFactor,
                        long squareDev,
                        long edgeThresh,
                        long corrections,
                        long shrink) {
      this.minEdgeFactor = minEdgeFactor;
      this.maxEdgeFactor = maxEdgeFactor;
      this.scanGapFactor = scanGapFactor;
      this.squareDev = squareDev;
      this.edgeThresh = edgeThresh;
      this.corrections = corrections;
      this.shrink = shrink;
   }

   /**
    * See {@link #DEFAULT_MIN_EDGE_FACTOR}.
    *
    * @return the value for this setting.
    */
   public double getMinEdgeFactor() {
      return minEdgeFactor;
   }

   /**
    * See {@link #DEFAULT_MAX_EDGE_FACTOR}.
    *
    * @return the value for this setting.
    */
   public double getMaxEdgeFactor() {
      return maxEdgeFactor;
   }

   /**
    * See {@link #DEFAULT_SCAN_GAP_FACTOR}.
    *
    * @return the value for this setting.
    */
   public double getScanGapFactor() {
      return scanGapFactor;
   }

   /**
    * See {@link #DEFAULT_SQUARE_DEV}.
    *
    * @return the value for this setting.
    */
   public long getSquareDev() {
      return squareDev;
   }

   /**
    * See {@link #DEFAULT_EDGE_THRESH}.
    *
    * @return the value for this setting.
    */
   public long getEdgeThresh() {
      return edgeThresh;
   }

   /**
    * See {@link #DEFAULT_CORRECTIONS}.
    *
    * @return the value for this setting.
    */
   public long getCorrections() {
      return corrections;
   }

   /**
    * See {@link #DEFAULT_SHRINK}.
    *
    * @return the value for this setting.
    */
   public long getShrink() {
      return shrink;
   }

   /**
    * Factory method with default settings.
    *
    * @return Returns decode options object with default values.
    */
   public static DecodeOptions getDefaultDecodeOptions() {
      return new DecodeOptions(DEFAULT_MIN_EDGE_FACTOR,
                               DEFAULT_MAX_EDGE_FACTOR,
                               DEFAULT_SCAN_GAP_FACTOR,
                               DEFAULT_SQUARE_DEV,
                               DEFAULT_EDGE_THRESH,
                               DEFAULT_CORRECTIONS,
                               DEFAULT_SHRINK);
   }
}
