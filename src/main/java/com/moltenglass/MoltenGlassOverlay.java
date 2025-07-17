package com.moltenglass;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class MoltenGlassOverlay extends Overlay
{
    private final MoltenGlassPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public MoltenGlassOverlay(MoltenGlassPlugin plugin)
    {
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT); // You can change to DYNAMIC or BOTTOM_RIGHT etc.
        setLayer(OverlayLayer.ABOVE_WIDGETS);  // Above widgets is standard for overlays

        panelComponent.setPreferredSize(new Dimension(80,0));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        int count = plugin.getMoltenGlassCount();
        long remaining = plugin.getMoltenGlassTimeRemaining();

        if (count > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Glass")
                    .right(String.valueOf(count))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time")
                    .right(remaining + "s")
                    .build());
        }

        return panelComponent.render(graphics);
    }

}