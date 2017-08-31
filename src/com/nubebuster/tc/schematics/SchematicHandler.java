package com.nubebuster.tc.schematics;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class SchematicHandler {

	public static Schematic loadSchematic(File file) {
		final Schematic schem;
		try {
			InputStream fis = new FileInputStream(file);
			NBTTagCompound nbtdata = NBTCompressedStreamTools.a(fis);

			short width = nbtdata.getShort("Width");
			short height = nbtdata.getShort("Height");
			short length = nbtdata.getShort("Length");

			byte[] blocks = nbtdata.getByteArray("Blocks");
			byte[] data = nbtdata.getByteArray("Data");

			// NBTTagList entities = nbtdata.getList("Entities");
			// NBTTagList tileentities = nbtdata.getList("TileEntities");

			fis.close();
			schem = new Schematic(blocks, data, width, length, height);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return schem;
	}
}
