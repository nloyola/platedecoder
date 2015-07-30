package org.biobank.platedecoder.ui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Code borrowed from:
 *   https://community.oracle.com/thread/2541811
 */
public class ZoomingPane extends ScrollPane {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ZoomingPane.class);

    private static final double ZOOM_IN = 1.1;

    private static final double ZOOM_OUT = 1.0 / ZOOM_IN;

    public ZoomingPane(Node content) {
        final StackPane zoomPane = new StackPane();
        zoomPane.getChildren().add(content);

        final Group scrollContent = new Group(zoomPane);
        setContent(scrollContent);

        viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
                zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
            });

        zoomPane.addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
                double scaleFactor = (event.getDeltaY() > 0) ? ZOOM_IN : ZOOM_OUT;

                // amount of scrolling in each direction in scrollContent coordinate units
                Point2D scrollOffset = figureScrollOffset(scrollContent);

                double scale = content.getScaleX() * scaleFactor;
                content.setScaleX(scale);
                content.setScaleY(scale);

                // move viewport so that old center remains in the center after the scaling
                repositionScroller(scrollContent, scaleFactor, scrollOffset);

                event.consume();
            });

        // Panning via drag....
        final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
        scrollContent.setOnMousePressed(event -> {
                lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
            });

        scrollContent.setOnMouseDragged(event -> {
                final Point2D mouseCoords = lastMouseCoordinates.get();
                final Bounds bounds = getViewportBounds();
                final Bounds scrollContentBounds = scrollContent.getLayoutBounds();
                double hValue, vValue;

                double deltaX = event.getX() - mouseCoords.getX();
                double extraWidth = scrollContentBounds.getWidth() - bounds.getWidth();
                if (extraWidth > 0.0) {
                    double deltaH = deltaX * (getHmax() - getHmin()) / extraWidth;
                    double desiredH = getHvalue() - deltaH;
                    hValue = Math.max(0, Math.min(getHmax(), desiredH));
                } else {
                    hValue = 0.0;
                }
                setHvalue(hValue);

                double deltaY = event.getY() - mouseCoords.getY();
                double extraHeight = scrollContentBounds.getHeight() - bounds.getHeight();
                if (extraHeight > 0.0) {
                    double deltaV = deltaY * (getHmax() - getHmin()) / extraHeight;
                    double desiredV = getVvalue() - deltaV;
                    vValue = Math.max(0, Math.min(getVmax(), desiredV));
                } else {
                    vValue = 0.0;
                }
                setVvalue(vValue);
            });
    }

    private Point2D figureScrollOffset(Node scrollContent) {
        final Bounds bounds = getViewportBounds();
        final Bounds scrollContentBounds = scrollContent.getLayoutBounds();

        double extraWidth = scrollContentBounds.getWidth() - bounds.getWidth();
        double hScrollProportion = (getHvalue() - getHmin()) / (getHmax() - getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContentBounds.getHeight() - bounds.getHeight();
        double vScrollProportion = (getVvalue() - getVmin()) / (getVmax() - getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    private void repositionScroller(Node scrollContent,
                                    double scaleFactor,
                                    Point2D scrollOffset) {
        final Bounds bounds = getViewportBounds();
        final Bounds scrollContentBounds = scrollContent.getLayoutBounds();

        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContentBounds.getWidth() - bounds.getWidth();
        double hValue, vValue;

        if (extraWidth > 0) {
            double halfWidth = bounds.getWidth() / 2 ;
            double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
            hValue = getHmin() + newScrollXOffset * (getHmax() - getHmin()) / extraWidth;
        } else {
            hValue = getHmin();
        }
        setHvalue(hValue);

        double extraHeight = scrollContentBounds.getHeight() - bounds.getHeight();
        if (extraHeight > 0) {
            double halfHeight = bounds.getHeight() / 2 ;
            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            vValue = getVmin() + newScrollYOffset * (getVmax() - getVmin()) / extraHeight;
        } else {
            vValue = getVmin();
        }
        setVvalue(vValue);

        //LOG.debug("repositionScroller: hValue: {}, vValue: {}", hValue, vValue);
    }

}
