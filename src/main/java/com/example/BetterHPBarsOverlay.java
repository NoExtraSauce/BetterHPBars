package com.example;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.NPC;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.api.Perspective;
import net.runelite.api.VarClientInt;


@Slf4j
public class BetterHPBarsOverlay extends Overlay
{
    private final Client client;
    private final BetterHPBarsPlugin plugin;
    private final NPCManager npcManager;
    private final BetterHPBarsConfig config;

    @Inject
    private BetterHPBarsOverlay(Client client, BetterHPBarsPlugin plugin, NPCManager npcManager, BetterHPBarsConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.npcManager = npcManager;
        this.config = config;

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

        LocalPoint lp = opponent.getLocalLocation();
        if (lp == null)
        {
            return null;
        }

        String hpText = health + " HP";
        String nameText = opponent.getName();

        int zoom = client.isResized()
                ? client.getVarcIntValue(VarClientInt.CAMERA_ZOOM_RESIZABLE_VIEWPORT)
                : client.getVarcIntValue(VarClientInt.CAMERA_ZOOM_FIXED_VIEWPORT);

        int zOffset = zoom > 500 ? 245 + config.verticalOffset() : 265 + config.verticalOffset();

        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        net.runelite.api.Point rawPoint = Perspective.getCanvasImageLocation(client, lp, dummyImage, zOffset);
        if (rawPoint == null)
        {
            return null;
        }

        java.awt.Point canvasPoint = new java.awt.Point(rawPoint.getX(), rawPoint.getY() + config.verticalOffset());


        // Font based on config
        int fontStyle = config.boldText() ? Font.BOLD : Font.PLAIN;
        Font hpFont = new Font("Arial", fontStyle, config.hpFontSize());
        Font nameFont = new Font("Arial", fontStyle, config.nameFontSize());

        // Opponents HP Count Font Controls
        graphics.setFont(hpFont);
        FontMetrics hpFm = graphics.getFontMetrics(hpFont);
        int hpWidth = hpFm.stringWidth(hpText);
        int hpAscent = hpFm.getAscent();
        int xHP = canvasPoint.x - (hpWidth / 2);
        int yHP = canvasPoint.y + (hpAscent / 2); // This approximates vertical center alignment
        graphics.setColor(Color.BLACK);
        graphics.drawString(hpText, xHP + 1, yHP + 1);
        graphics.setColor(config.hpColor());
        graphics.drawString(hpText, xHP, yHP);


        // Opponents Name Font Controls
        graphics.setFont(nameFont);
        FontMetrics nameFm = graphics.getFontMetrics(nameFont);
        int nameWidth = nameFm.stringWidth(nameText);
        int nameAscent = nameFm.getAscent();
        int xName = canvasPoint.x - (nameWidth / 2);
        int yName = yHP - nameAscent - 2; // 2px spacing above HP text
        graphics.setColor(Color.BLACK);
        graphics.drawString(nameText, xName + 1, yName + 1);
        graphics.setColor(config.nameColor());
        graphics.drawString(nameText, xName, yName);

        return null;
    }
}
