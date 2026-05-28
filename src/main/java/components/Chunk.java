package components;

import org.joml.Vector2i;

import static components.Terrain.CHUNK_SIZE;

public class Chunk extends Object {
    public static final float HEIGHT = 50.0f;

    protected Vector2i posIndex;

    // For batching, rather be converted to FloatBuffer & IntBuffer respectively
    public float[] vertexArray;
    public int[] elementArray;

    public Chunk(String ID, Vector2i posIndex, float[][] heights) {
        super(ID);

        this.posIndex = posIndex;

        int hx = posIndex.x + Terrain.getHalfWidth();
        int hy = posIndex.y + Terrain.getHalfHeight();

        float bl = heights[hx][hy];
        float br = heights[hx + 1][hy];
        float tl = heights[hx][hy + 1];
        float tr = heights[hx + 1][hy + 1];

        float offsetX = CHUNK_SIZE * posIndex.x;
        float offsetY = CHUNK_SIZE * posIndex.y;

        this.vertexArray = new float[]{
            // 3f Vector                                          Color                       Texture
            offsetX + CHUNK_SIZE, offsetY,                br,     0.0f, 0.0f, 0.0f, 0.0f,     0.0f, 0.0f, // Bottom right
            offsetX + CHUNK_SIZE, offsetY + CHUNK_SIZE,   tr,     1.0f, 0.0f, 0.0f, 0.0f,     0.0f, 0.0f, // Top Right
            offsetX,              offsetY + CHUNK_SIZE,   tl,     0.0f, 1.0f, 0.0f, 0.0f,     0.0f, 0.0f, // Top left
            offsetX,              offsetY,                bl,     0.0f, 0.0f, 1.0f, 0.0f,     0.0f, 0.0f, // Bottom left
        };

        // I'm thinking of making this more of an efficient use
        //  - somewhere on the outer layer
        this.elementArray = new int[]{
            0, 1, 2, // Top right triangle
            0, 2, 3 // Bottom left triangle
        };
    }
}
