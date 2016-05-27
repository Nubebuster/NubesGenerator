package com.nubebuster.tc;

import java.io.File;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R3.BiomeBase;

public class TerrainControl extends JavaPlugin {

	// TODO [water & lava lakes, caves, ores, ravines, mineshafts, pyramids,
	// village]

	@Override
	public void onLoad() {
		clear(new File("world"));

		Field biomesField;
		try {
			biomesField = BiomeBase.class.getDeclaredField("biomes");
			biomesField.setAccessible(true);

			if (biomesField.get(null) instanceof BiomeBase[]) {
				BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);
				for (int i = 0; i < biomes.length; i++)
					biomes[i] = null;
				biomes[BiomeBase.OCEAN.id] = BiomeBase.COLD_TAIGA;
				biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.SWAMPLAND;
				biomes[BiomeBase.COLD_TAIGA.id] = BiomeBase.COLD_TAIGA;
				biomes[BiomeBase.SWAMPLAND.id] = BiomeBase.SWAMPLAND;
				biomes[BiomeBase.SAVANNA.id] = BiomeBase.SAVANNA;
				biomes[BiomeBase.COLD_TAIGA.id] = BiomeBase.COLD_TAIGA;
				biomes[BiomeBase.PLAINS.id] = BiomeBase.PLAINS;
				biomes[BiomeBase.DESERT.id] = BiomeBase.DESERT;
				biomes[BiomeBase.BIRCH_FOREST.id] = BiomeBase.BIRCH_FOREST;
				biomes[BiomeBase.FOREST.id] = BiomeBase.FOREST;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onJoin(PlayerJoinEvent event) {
				event.getPlayer().setGameMode(GameMode.SPECTATOR);
			}
		}, this);
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new CustomGenerator();
	}

	private void clear(File file) {
		if (!file.exists())
			return;
		if (file.isFile()) {
			file.delete();
		} else {
			for (File f : file.listFiles())
				clear(f);
			file.delete();
		}
	}

}
