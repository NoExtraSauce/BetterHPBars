package com.example;

import lombok.extern.slf4j.Slf4j;
import java.awt.Point;
import net.runelite.api.*;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.RenderableEntity;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class BetterHPBarsOverlay extends Overlay
{
    private final Client client;
    private final BetterHPBarsPlugin plugin;
    private final NPCManager npcManager;

    @Inject
    private BetterHPBarsOverlay(Client client, BetterHPBarsPlugin plugin, NPCManager npcManager)
    {
        this.client = client;
        this.plugin = plugin;
        this.npcManager = npcManager;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Actor opponent = plugin.getLastOpponent();
        if (opponent == null || opponent.getHealthScale() <= 0)
        {
            return null;
        }

        int healthRatio = opponent.getHealthRatio();
        int healthScale = opponent.getHealthScale();

        Integer maxHealth = null;
        if (opponent instanceof NPC)
        {
            maxHealth = npcManager.getHealth(((NPC) opponent).getId());
        }

        if (maxHealth == null || healthRatio < 0)
        {
            return null;
        }

        int health = 0;
        if (healthRatio > 0)
        {
            int minHealth = 1;
            int maxHp;
            if (healthScale > 1)
            {
                if (healthRatio > 1)
                {
                    minHealth = (maxHealth * (healthRatio - 1) + healthScale - 2) / (healthScale - 1);
                }
                maxHp = (maxHealth * healthRatio - 1) / (healthScale - 1);
                if (maxHp > maxHealth)
                {
                    maxHp = maxHealth;
                }
            }
            else
            {
                maxHp = maxHealth;
            }
            health = (minHealth + maxHp + 1) / 2;
        }

        // Convert local point to screen coordinates
        LocalPoint lp = opponent.getLocalLocation();
        if (lp == null)
        {
            return null;
        }

        String text = Integer.toString(health);

        // Use RuneLites Perspective utility to get canvas position
        net.runelite.api.Point rlPoint = Perspective.getCanvasTextLocation(client, graphics, lp, text, 225);
        if (rlPoint == null)
        {
            return null;
        }

        // Center the text horizontally and vertically based on font metrics
        Font font = new Font("Arial", Font.BOLD, 14);
        graphics.setFont(font);
        FontMetrics fm = graphics.getFontMetrics(font);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int x = rlPoint.getX() - (textWidth / 2);
        int y = rlPoint.getY() + (textHeight / 2);

        // Draw outline and text
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 1);
        graphics.setColor(Color.WHITE);
        graphics.drawString(text, x, y);


        return null;
    }
}