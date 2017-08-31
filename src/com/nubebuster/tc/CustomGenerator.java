package com.nubebuster.tc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import com.nubebuster.tc.populators.CustomPopulator;
import com.nubebuster.tc.populators.OrePopulator;
import com.nubebuster.tc.populators.StructurePopulator;

public class CustomGenerator extends ChunkGenerator {

	private List<BlockPopulator> populators = new ArrayList<BlockPopulator>();

	public CustomGenerator() {
		populators.add(new StructurePopulator());
		populators.add(new CustomPopulator());
		populators.add(new OrePopulator());
	}
	
	@Override
	public boolean canSpawn(World world, int x, int z) {
		return super.canSpawn(world, x, z);
	}

	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		return new Location(world, 0, 32, 0);
	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return populators;
	}

	@SuppressWarnings("deprecation")
	@Override
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
		byte[][] result = new byte[world.getMaxHeight() >> 4][];
		int blockX = chunkX << 4, blockZ = chunkZ << 4;
		SimplexOctaveGenerator gen1 = new SimplexOctaveGenerator(world, 16/* 8 */);
		gen1.setScale(1 / 64.0);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				setBlock(result, x, 0, z, (byte) Material.BEDROCK.getId());
				int realX = x + blockX;
				int realZ = z + blockZ;
				double frequency = 0.2;
				double amplitude = 0.1;

				// PerlinNoiseGenerator png = new PerlinNoiseGenerator(random);
				Biome biome = biomes.getBiome(x, z);
				int multitude = 6;
				if (biome == Biome.COLD_TAIGA)
					multitude = 12;
				else if (biome == Biome.SAVANNA || biome == Biome.SWAMPLAND)
					multitude = 8;
				int sea_level = 55;

				double maxHeight = gen1.noise(realX, realZ, frequency, amplitude) * multitude + sea_level;

				if (biome == Biome.DEEP_OCEAN) {
					biome = Biome.SWAMPLAND;
					biomes.setBiome(x, z, Biome.SWAMPLAND);
				} else if (biome == Biome.OCEAN) {
					biome = Biome.COLD_TAIGA;
					biomes.setBiome(x, z, Biome.COLD_TAIGA);
				}

				byte semiTop = (byte) getSemiTopLayer(biome).getId(), top = (byte) getTopLayer(biome).getId();
				for (int y = 1; y < maxHeight - 4; y++)
					setBlock(result, x, y, z, (byte) Material.STONE.getId());
				for (int y = (int) maxHeight - 4; y < maxHeight - 1; y++) {
					// if (png.noise(x, y, z, 3, 1.0, amplitude) == 1)
					setBlock(result, x, y, z, semiTop);
				}
				setBlock(result, x, (int) maxHeight - 1, z, top);
			}
		}
		return result;
	}

	public static void main(String[] args) {
		SimplexNoiseGenerator sng = new SimplexNoiseGenerator(new Random(1));
		for (int i = 0; i < 10; i++) {
			System.out.println(sng.noise(i, i, i, 3, 1.2, 1, false));
		}
	}

	private void setBlock(byte[][] result, int x, int y, int z, byte id) {
		int index = y >> 4;
		if (result[index] == null)
			result[index] = new byte[4096];
		result[index][((y & 0xF) << 8) | (z << 4) | x] = id;
	}

	private Material getSemiTopLayer(Biome biome) {
		switch (biome) {
		case DESERT:
			return Material.SANDSTONE;
		default:
			return Material.DIRT;
		}
	}

	private Material getTopLayer(Biome biome) {
		switch (biome) {
		case DESERT:
			return Material.SAND;
		default:
			return Material.GRASS;
		}
	}
}
