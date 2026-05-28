package components;

import org.joml.Vector2i;

import java.util.ArrayList;
import com.raylabz.opensimplex.OpenSimplexNoise;
import renderer.Region;

public class Terrain {
    private final Region region = new Region();
    private long seed;

    private static Vector2i size2i = new Vector2i(160, 160); // Terrain area in chunks
    private float[][] heights;

    // Terrain scale
    private final int width = size2i.x * 2;
    private final int height = size2i.y * 2;

    // 50 units ~ 50px
    public static final float CHUNK_SIZE = 100.0f;
    private static final int NOISE_SIZE_FACTOR = 25; // Larger values create smaller biomes
    private boolean isGenerated;

    public Terrain(long seed) {
        this.isGenerated = false;
        this.seed = seed;
        this.heights = new float
            [width + 1]
            [height + 1];
    }

    public static int getHalfWidth() { return size2i.x; }
    public static int getHalfHeight() { return size2i.y; }

    /**
     * Generates chunks as game objects
     * before
     */
    public void init() {
        OpenSimplexNoise noise = new OpenSimplexNoise(seed) ;
        if (!this.isGenerated) {
            this.isGenerated = true;

            // Using Perlin noise to generate heights
            for (int x = 0; x <= width; x++) {
                for (int y = 0; y <= height; y++) {
                    heights[x][y] =
                        (float)(noise.getNoise2D(x * NOISE_SIZE_FACTOR,y * NOISE_SIZE_FACTOR).getValue() * -Chunk.HEIGHT);
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
                region.batchChunk(newChunk);
            }
        }
        region.init();
    }

    // Updating chunks with DeltaTime
    public void update(float dt) {
        region.render(dt);
    }
}
