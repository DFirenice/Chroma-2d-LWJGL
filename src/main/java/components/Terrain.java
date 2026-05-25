package components;

import org.joml.Vector2i;

import java.util.ArrayList;

public class Terrain {
    private static Vector2i size2i = new Vector2i(10, 10); // Terrain area in chunks
    private float[][] heights;

    // Terrain scale
    private final int width = size2i.x * 2;
    private final int height = size2i.y * 2;

    private ArrayList<Chunk> chunks = new ArrayList<>((int)(width * height));

    // 50 units ~ 50px
    public static final float CHUNK_SIZE = 100.0f;
    private boolean isGenerated;

    public Terrain() {
        this.isGenerated = false;
        this.heights = new float
            [width + 1]
            [height + 1];
    }

    public static int getHalfWidth() { return size2i.x; }
    public static int getHalfHeight() { return size2i.y; }

    /**
     * Generates chunks as game objects
     */
    public void init() {
        if (!this.isGenerated) {
            this.isGenerated = true;

            // Later will use Perlin noise
            for (int x = 0; x <= width; x++) {
                for (int y = 0; y <= height; y++) {
                    heights[x][y] =
                        (float)(Math.random() * -50.0f);
                }
            }

            generateChunks();
        }
    }

    private void generateChunks() {
        // Generating chunks for the plane with modifications on depth
        for (int x = -size2i.x; x < size2i.x; x++) {
            for (int y = -size2i.y; y < size2i.y; y++) {
                Chunk newChunk = new Chunk("chunk(" + x + ", " + y + ")", new Vector2i(x, y), heights);
                chunks.add(newChunk);
                newChunk.init();
            }
        }
    }

    /**
     * Updating chunks with DeltaTime
     */
    public void update(float dt) {
        chunks.forEach(chunk -> {
           chunk.update(dt);
        });
    }
}
