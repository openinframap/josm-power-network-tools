package org.openinframap.powernetworktools;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.actions.mapmode.MapMode;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.ILatLon;

import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Shortcut;

import org.openstreetmap.josm.gui.layer.MapViewPaintable;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;

/**
 * Map mode for estimating voltage based on distance.
 * 
 * Clicking both sides of a busbar will draw a line on the map and tell the
 * dialog to estimate the voltage.
 */
public class VoltageEstimatorMode extends MapMode {

    private static final long serialVersionUID = 53895645438324L;

    private static final Cursor CURSOR = ImageProvider.getCursor("crosshair", "voltage");

    private VoltageEstimatorLayer voltageLayer = new VoltageEstimatorLayer();
    private List<ILatLon> points = new ArrayList<>(2);

    public VoltageEstimatorMode() {
        super("Voltage estimator", "voltage", "Voltage estimator mode",
                Shortcut.registerShortcut("mapmode:voltageestimator", "Map mode: Voltage Estimator",
                        KeyEvent.VK_V, Shortcut.DIRECT),
                CURSOR);
        putValue("toolbar", "voltage");
    }

    @Override
    public void enterMode() {
        super.enterMode();
        MainApplication.getMap().mapView.addTemporaryLayer(voltageLayer);
        MainApplication.getMap().mapView.addMouseListener(this);
        PowerNetworkToolsPlugin.voltageDialog.clearVoltage();
        PowerNetworkToolsPlugin.voltageDialog.unfurlDialog();
    }

    @Override
    public void exitMode() {
        super.exitMode();
        MainApplication.getMap().mapView.removeMouseListener(this);
        MainApplication.getMap().mapView.removeTemporaryLayer(voltageLayer);
        points.clear();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // Left click adds a point to the measurement
            if (points.size() > 1) {
                // Clicking a third point starts a new measurement
                points.clear();
            }
            points.add(MainApplication.getMap().mapView.getLatLon(e.getX(), e.getY()));
        } else {
            return;
        }

        if (points.size() == 2) {
            double distance = points.get(0).greatCircleDistance(points.get(1));
            PowerNetworkToolsPlugin.voltageDialog.setVoltageDistance(distance);
            PowerNetworkToolsPlugin.voltageDialog.unfurlDialog();
        } else {
            PowerNetworkToolsPlugin.voltageDialog.clearVoltage();
        }

        MainApplication.getMap().mapView.repaint();
    }

    /*
     * Temporary layer to render the selected distance.
     */
    private class VoltageEstimatorLayer implements MapViewPaintable {

        @Override
        public void paint(Graphics2D g, final MapView mv, Bounds bounds) {
            g.setColor(Color.green);
            g.setStroke(new java.awt.BasicStroke(2));

            if (points.size() == 0) {
                return;
            }

            Point p1 = mv.getPoint(points.get(0));
            g.drawOval(p1.x - 2, p1.y - 2, 4, 4);

            if (points.size() == 1) {
                return;
            }

            Point p2 = mv.getPoint(points.get(1));
            g.drawOval(p2.x - 2, p2.y - 2, 4, 4);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}