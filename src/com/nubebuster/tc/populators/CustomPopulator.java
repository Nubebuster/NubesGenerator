package com.nubebuster.tc.populators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.generator.BlockPopulator;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.WorldGenLakes;

public class CustomPopulator extends BlockPopulator {

	@SuppressWarnings("deprecation")
	@Override
	public void populate(World world, Random random, Chunk source) {
		int chunkX = source.getX() << 4, chunkZ = source.getZ() << 4;
		Biome simpleBiome = source.getBlock(8, 0, 8).getBiome();
		int treeFreq = simpleBiome == Biome.SWAMPLAND || simpleBiome == Biome.DESERT ? 8 : 12;
		if (simpleBiome == Biome.SWAMPLAND)
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					Biome b = world.getBiome(chunkX + x, chunkZ + z);
					if (b == Biome.SWAMPLAND && chance(random, 64)) {
						Material mush = random.nextBoolean() ? Material.BROWN_MUSHROOM : Material.RED_MUSHROOM;
						if (chance(random, 8)) {
							int amount = random.nextInt(6) + 14;
							for (int nr = 0; nr < amount; nr++) {
								int ttx = chunkX + x + (int) (random.nextGaussian() * 12),
										ttz = chunkZ + z + (int) (random.nextGaussian() * 12);
								placePlant(world, ttx, ttz, mush.getId(), (byte) 0);
							}
						}
					}
				}
			}
		for (int i = 0; i < random.nextInt(treeFreq); i++) {
			int x = random.nextInt(16) + chunkX + 1, z = random.nextInt(16) + chunkZ + 1;
			Biome biome = world.getBiome(x, z);
			if (biome == Biome.DESERT) {
				Block c = world.getBlockAt(x, world.getHighestBlockYAt(x, z), z);
				c.setType(Material.CACTUS);
				if (random.nextBoolean())
					c.getRelative(BlockFace.UP).setType(Material.CACTUS);
			} else {
				TreeType type = getTreeType(biome, random);
				if (type != null)
					placeTree(world, random, x, z, type);
			}
		}
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Biome b = world.getBiome(chunkX + x, chunkZ + z);
				if (b == Biome.COLD_TAIGA) {
					Block bb = world.getHighestBlockAt(chunkX + x, chunkZ + z);
					bb.setType(Material.SNOW);
				} else {
					if (chance(random, 16)) {
						if ((b == Biome.FOREST || b == Biome.BIRCH_FOREST || b == Biome.PLAINS) && chance(random, 6))
							placePlant(world, chunkX + x, chunkZ + z,
									random.nextBoolean() ? Material.YELLOW_FLOWER.getId() : Material.RED_ROSE.getId(),
									(byte) 0);
						else if (b == Biome.DESERT) {
							if (chance(random, 12))
								placePlant(world, chunkX + x, chunkZ + z, Material.LONG_GRASS.getId(), (byte) 0);
						} else
							placePlant(world, chunkX + x, chunkZ + z, Material.LONG_GRASS.getId(), (byte) 1);
					}
				}
			}
		}
		if (chance(random, 8)) {
			WorldGenLakes wgl = new WorldGenLakes(random.nextBoolean() ? Blocks.WATER : Blocks.LAVA);
			int x = chunkX + random.nextInt(16), z = chunkZ + random.nextInt(16);
			wgl.generate(((CraftWorld) world).getHandle(), random,
					new BlockPosition(x, world.getHighestBlockYAt(x, z), z));
		}
		if (chance(random, 100)) {
			int x = chunkX + random.nextInt(16), z = chunkZ + random.nextInt(16);
			int y = world.getHighestBlockYAt(x, z);
			for (int i = 0; i < 32; i++) {
				int j = x + random.nextInt(8) - random.nextInt(8);
				int k = y + random.nextInt(4) - random.nextInt(4);
				int m = z + random.nextInt(8) - random.nextInt(8);

				Block block = world.getBlockAt(j, k, m);
				Block below = world.getBlockAt(j, k - 1, m);
				if ((block.getType() == Material.AIR) && (below.getType() == Material.GRASS)) {
					block.setTypeIdAndData(Material.PUMPKIN.getId(), (byte) random.nextInt(4), false);
				}
			}
		}
		/*
		 * if (chance(random, 24)) { int x = random.nextInt(16) + chunkX, z =
		 * random.nextInt(16) + chunkZ; WorldGenVillage vill = new
		 * WorldGenVillage(); vill.a(((CraftWorld) world).getHandle(), new
		 * BlockPosition(x, world.getHighestBlockYAt(x, z), z)); }
		 */
		/*
		 * if (chance(random, 6)) { int x = chunkX + random.nextInt(16), z =
		 * chunkZ + random.nextInt(16); WorldGenCaves wgc = new WorldGenCaves();
		 * // .worldProvider.getChunkProvider() ChunkSnapshot csnap = new
		 * ChunkSnapshot();
		 * 
		 * try { Field f = ChunkSnapshot.class.getDeclaredField("a");
		 * f.setAccessible(true); short[] data = (short[]) f.get(csnap); for
		 * (int xx = 0; x < 16; x++) { for (int zz = 0; z < 16; z++) { for (int
		 * y = 0; y < 128; y++) { data[xx + y + zz] = (short)
		 * source.getChunkSnapshot().getBlockTypeId(xx, y, zz); } } } } catch
		 * (Exception e) { e.printStackTrace(); } //
		 * chunkProviderServer.chunkProvider wgc.a(((CraftWorld)
		 * world).getHandle().worldProvider.getChunkProvider(), ((CraftWorld)
		 * world).getHandle(), x, z, csnap); }
		 */

		/*
		 * if (chance(random, 25)) { int x = chunkX + random.nextInt(16), y =
		 * random.nextInt(30) + 20, z = chunkZ + random.nextInt(16);
		 * WorldGenDungeons dgen = new WorldGenDungeons();
		 * dgen.generate(((CraftWorld) world).getHandle(), random, new
		 * BlockPosition(x, world.getHighestBlockYAt(x, z), z)); }
		 */

		// if(chance(random, 10)) {
		// int x = chunkX + random.nextInt(16), y = random.nextInt(30) + 20 +
		// 50, z = chunkZ + random.nextInt(16);
		// WorldGenMineshaft ms = new WorldGenMineshaft();
		// ms.a(((CraftWorld) world).getHandle(), new BlockPosition(x, y, z));
		// }
	}

	@SuppressWarnings("deprecation")
	private void placePlant(World world, int x, int z, int id, byte data) {
		for (int i = world.getMaxHeight(); i > 0; i--) {
			Material mat = world.getBlockAt(x, i, z).getType();
			if (mat == Material.LOG || mat == Material.LOG_2)
				break;
			if (mat.isSolid() && mat != Material.LEAVES && mat != Material.LEAVES_2) {
				world.getBlockAt(x, i + 1, z).setTypeIdAndData(id, data, false);
				break;
			}
		}
	}

	private void placeTree(World world, Random random, int x, int z, TreeType type) {
		Location l = new Location(world, x, world.getHighestBlockYAt(x, z), z);
		world.getBlockAt(l).setType(Material.AIR);
		world.generateTree(l, type);
	}

	private TreeType getTreeType(Biome biome, Random random) {
		switch (biome) {
		case SWAMPLAND:
			return TreeType.SWAMP;
		case BIRCH_FOREST:
			return TreeType.BIRCH;
		case JUNGLE:
			return chance(random, 3) ? TreeType.SMALL_JUNGLE : TreeType.JUNGLE;
		case COLD_TAIGA:
			return chance(random, 2) ? TreeType.REDWOOD : TreeType.MEGA_REDWOOD;
		case PLAINS:
			return null;
		case DESERT:
			return null;
		case SAVANNA:
			return TreeType.ACACIA;
		default:
			return TreeType.TREE;
		}
	}

	private boolean chance(Random random, int samples) {
		return random.nextInt(samples) == 0;
	}
}