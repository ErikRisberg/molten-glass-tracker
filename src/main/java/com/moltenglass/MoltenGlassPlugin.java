
package com.moltenglass;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.TileItem;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.ItemDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;

@Slf4j
@PluginDescriptor(
		name = "Molten Glass Tracker"
)
public class MoltenGlassPlugin extends Plugin
{
	private static final int MOLTEN_GLASS_ID = 1775;
	private static final int DESPAWN_TIME_SECONDS = 180;

	private int moltenGlassCount = 0;
	private long moltenGlassStartTime = 0;

	@Inject
	private Client client;

	@Inject
	private MoltenGlassConfig config;

	@Inject
	private MoltenGlassOverlay overlay;

	@Inject
	private OverlayManager overlayManager;


	@Override
	protected void startUp() throws Exception
	{
		log.info("Molten Glass Tracker started!");
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Molten Glass Tracker stopped!");
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned event)
	{
		TileItem item = event.getItem();
		if (item.getId() == MOLTEN_GLASS_ID)
		{
			moltenGlassCount += item.getQuantity();
			if (moltenGlassCount > 0 && moltenGlassStartTime == 0)
			{
				moltenGlassStartTime = Instant.now().getEpochSecond();
			}
		}
	}


	@Subscribe
	public void onItemDespawned(ItemDespawned event)
	{
		TileItem item = event.getItem();
		if (item.getId() == MOLTEN_GLASS_ID)
		{
			moltenGlassCount -= item.getQuantity();
			if (moltenGlassCount <= 0)
			{
				moltenGlassCount = 0;
				moltenGlassStartTime = 0; // Reset the timer
			}
		}
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.LOGIN_SCREEN)
		{
			moltenGlassCount = 0;
		}
	}

	public int getMoltenGlassCount()
	{
		return moltenGlassCount;
	}

	public long getMoltenGlassTimeRemaining()
	{
		if (moltenGlassStartTime == 0) return 0;
		long elapsed = Instant.now().getEpochSecond() - moltenGlassStartTime;
		long remaining = DESPAWN_TIME_SECONDS - elapsed;
		return Math.max(remaining, 0);
	}


	@Provides
	MoltenGlassConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MoltenGlassConfig.class);
	}
}
