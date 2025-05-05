package org.openinframap.powernetworktools;

import org.openstreetmap.josm.gui.IconToggleButton;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class PowerNetworkToolsPlugin extends Plugin {
    
    protected static VoltageEstimatorDialog voltageDialog;
    private IconToggleButton btn;
    private VoltageEstimatorMode voltageMode;
    
    public PowerNetworkToolsPlugin(PluginInformation info) {
        super(info);
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (newFrame != null) {
            newFrame.addToggleDialog(voltageDialog = new VoltageEstimatorDialog());
            voltageMode = new VoltageEstimatorMode();
            btn = new IconToggleButton(voltageMode);
            btn.setVisible(true);
            newFrame.addMapMode(btn);
        } else {
            voltageDialog = null;
            voltageMode = null;
        }
    }
}