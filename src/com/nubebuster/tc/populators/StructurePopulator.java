package com.nubebuster.tc.populators;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import com.nubebuster.tc.TerrainControl;
import com.nubebuster.tc.schematics.Schematic;
import com.nubebuster.tc.schematics.SchematicHandler;

public class StructurePopulator extends BlockPopulator {

	private Schematic house;

	public StructurePopulator() {
		house = SchematicHandler.loadSchematic(new File(TerrainControl.inst.getDataFolder(), "house.schematic"));
	}

	@Override
	public void populate(World world, Random random, Chunk source) {
		int chunkX = source.getX() << 4, chunkZ = source.getZ() << 4;
		Biome simpleBiome = source.getBlock(8, 0, 8).getBiome();
		if ((simpleBiome == Biome.FOREST || simpleBiome == Biome.PLAINS || simpleBiome == Biome.BIRCH_FOREST
				|| simpleBiome == Biome.DESERT) && random.nextInt(96) == 0) {
			int x = chunkX + random.nextInt(16), z = chunkZ + random.nextInt(16);
			// world.getHighestBlockAt(chunkX + x, chunkZ +
			// z).setType(Material.DIAMOND_BLOCK);
			Location loc = world.getHighestBlockAt(x, z).getLocation();
			pasteSchematic(loc, house);
		}
	}

	private void pasteSchematic(Location loc, Schematic schematic) {
		World world = loc.getWorld();
		byte[] blocks = schematic.getBlocks();
		byte[] blockData = schematic.getData();

		short length = schematic.getLenght();
		short width = schematic.getWidth();
		short height = schematic.getHeight();

		List<Block> subPlace = new ArrayList<Block>();
		List<Short> subPlaceId = new ArrayList<Short>();
		List<Byte> subPlaceData = new ArrayList<Byte>();

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				for (int z = 0; z < length; ++z) {
					int index = y * width * length + z * width + x;
					Block block = new Location(world, x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
					if (Math.abs(blocks[index]) == 0)// noAir
						continue;
					short b = (short) (blocks[index] & 0x00ff);
					try {
						@SuppressWarnings("deprecation")
						Material mat = Material.getMaterial(b);
						if (mat == Material.SPONGE) {
							place(block, (short) 0, (byte) 0);
							continue;
						} else if (mat == Material.TORCH || mat == Material.REDSTONE_TORCH_OFF
								|| mat == Material.REDSTONE_TORCH_ON) {
							subPlace.add(block);
							subPlaceId.add(b);
							subPlaceData.add(blockData[index]);
						} else if (mat == Material.BREWING_STAND || mat == Material.DISPENSER || mat == Material.DROPPER
								|| mat == Material.FURNACE || mat == Material.CHEST || mat == Material.BURNING_FURNACE
								|| mat == Material.WALL_SIGN || mat == Material.SIGN_POST || mat == Material.JUKEBOX) {
							place(block, b, (byte) -69);
							continue;
						}
					} catch (Exception e) {
						System.out.println(blocks[index] + " was removed");
					}
					place(block, b, blockData[index]);
				}
			}
		}
		for (int i = 0; i < subPlace.size(); i++)
			place(subPlace.get(i), subPlaceId.get(i), subPlaceData.get(i));
	}

	@SuppressWarnings("deprecation")
	private void place(Block b, short id, byte data) {
		b.setTypeIdAndData(id, data, false);
	}
}
