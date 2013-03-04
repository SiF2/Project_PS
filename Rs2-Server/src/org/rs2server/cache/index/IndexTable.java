package org.rs2server.cache.index;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.rs2server.cache.Archive;
import org.rs2server.cache.Cache;
import org.rs2server.cache.index.impl.MapIndex;
import org.rs2server.cache.index.impl.StandardIndex;

/**
 * The <code>IndexTable</code> class manages all the <code>Index</code>es in the
 * <code>Cache</code>.
 * 
 * @author Graham Edgecombe
 * 
 */
public class IndexTable {

	/**
	 * The map indices.
	 */
	private MapIndex[] mapIndices;

	/**
	 * The object def indices.
	 */
	private StandardIndex[] objectDefinitionIndices;

	/**
	 * Creates the index table.
	 * 
	 * @param cache
	 *            The cache.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public IndexTable(Cache cache) throws IOException {
		Archive configArchive = new Archive(cache.getFile(0, 2));
		initObjectDefIndices(configArchive);

		Archive versionListArchive = new Archive(cache.getFile(0, 5));
		initMapIndices(versionListArchive);
	}

	/**
	 * Gets a single map index.
	 * 
	 * @param area
	 *            The area id.
	 * @return The map index, or <code>null</code> if the area does not exist.
	 */
	public MapIndex getMapIndex(int area) {
		for (MapIndex index : mapIndices) {
			if (index.getIdentifier() == area) {
				return index;
			}
		}
		return null;
	}

	/**
	 * Gets all of the map indices.
	 * 
	 * @return The map indices array.
	 */
	public MapIndex[] getMapIndices() {
		return mapIndices;
	}

	/**
	 * Gets a single object definition index.
	 * 
	 * @param object
	 *            The object id.
	 * @return The index.
	 */
	public StandardIndex getObjectDefinitionIndex(int object) {
		return objectDefinitionIndices[object];
	}

	/**
	 * Gets all of the object definition indices.
	 * 
	 * @return The object definition indices array.
	 */
	public StandardIndex[] getObjectDefinitionIndices() {
		return objectDefinitionIndices;
	}

	/**
	 * Initialises the map indices.
	 * 
	 * @param versionListArchive
	 *            The version list archive.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private void initMapIndices(Archive versionListArchive) throws IOException {
		ByteBuffer buf = versionListArchive.getFileAsByteBuffer("map_index");
		int indices = buf.remaining() / 7;
		mapIndices = new MapIndex[indices];
		for (int i = 0; i < indices; i++) {
			int area = buf.getShort() & 0xFFFF;
			int mapFile = buf.getShort() & 0xFFFF;
			int landscapeFile = buf.getShort() & 0xFFFF;
			boolean members = (buf.get() & 0xFF) == 1;
			MapIndex index = new MapIndex(area, mapFile, landscapeFile, members);
			mapIndices[i] = index;
		}
	}

	/**
	 * Initialises the object definition indices.
	 * 
	 * @param configArchive
	 *            The config archive.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private void initObjectDefIndices(Archive configArchive) throws IOException {
		ByteBuffer buf = configArchive.getFileAsByteBuffer("loc.idx");
		int objectCount = buf.getShort() & 0xFFFF;
		objectDefinitionIndices = new StandardIndex[objectCount];
		int offset = 2;
		for (int id = 0; id < objectCount; id++) {
			objectDefinitionIndices[id] = new StandardIndex(id, offset);
			offset += buf.getShort() & 0xFFFF;
		}
	}

}