package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Opponent Overhead HP Counter",
        description = "Displays Overhead HP Counters On Opponents ",
        tags = {"combat", "hp", "overlay", "health"}
)
public class BetterHPBarsPlugin extends Plugin
{
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private BetterHPBarsOverlay overlay;

    @Getter
    private Actor lastOpponent;
    private Instant lastInteractedTime;

    @Provides
    BetterHPBarsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(BetterHPBarsConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        lastOpponent = null;
        lastInteractedTime = null;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() != GameState.LOGGED_IN)
        {
            lastOpponent = null;
            lastInteractedTime = null;
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event)
    {
        if (event.getSource() != client.getLocalPlayer())
            return;

        Actor opponent = event.getTarget();

        if (opponent == null)
        {
            lastInteractedTime = Instant.now();
            return;
        }

        lastOpponent = opponent;
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        if (lastOpponent != null && lastInteractedTime != null
                && client.getLocalPlayer().getInteracting() == null)
        {
            if (Duration.between(lastInteractedTime, Instant.now()).compareTo(TIMEOUT) > 0)
            {
                lastOpponent = null;
            }
        }
    }
}
