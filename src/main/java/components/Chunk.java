package components;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import utils.Logger;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static components.Terrain.CHUNK_SIZE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Chunk extends Object {
    // Debug
    private Logger logger = new Logger(this.getClass());
    private String chunkID;

    protected Vector2i posIndex;
    private float offsetX, offsetY;

    protected float[] vertexArray;
    private int[] elementArray;

    private int vaoID, vboID, eboID;

    // Tets for a dynamic value
    private FloatBuffer vertexBuffer;
    private IntBuffer elementBuffer;

    private static final int positionSize = 3;
    private static final int colorSize = 4;
    private static final int uvSize = 2;
    protected static final int vertexSize = (positionSize + colorSize + uvSize);

    public Chunk(String chunkID, Vector2i posIndex, float[][] heights) {
        super(chunkID);
        this.chunkID = chunkID;
        this.posIndex = posIndex;

        //assert posIndex.x != 0 && posIndex.y != 0 : "Error: Invalid chunk coordinates - (" + posIndex.x + ", " + posIndex.y + ")";

        int hx = posIndex.x + Terrain.getHalfWidth();
        int hy = posIndex.y + Terrain.getHalfHeight();

        float bl = heights[hx][hy];
        float br = heights[hx + 1][hy];
        float tl = heights[hx][hy + 1];
        float tr = heights[hx + 1][hy + 1];

        offsetX = CHUNK_SIZE * posIndex.x;
        offsetY = CHUNK_SIZE * posIndex.y;

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

    @Override
    public void init() {
        // Creating VAO, VBO, and EBO objects, and sending to the GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // OpenGL expects buffer, so we create buffer
        vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray);
        vertexBuffer.flip(); // Finalizing the buffer for reading

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Creating indices and uploading
        elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray);
        elementBuffer.flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Adding vertex attribute pointers
        // (basically size in memory of one stride vertex element)
        int vertexSizeBytes = vertexSize * Float.BYTES;

        // What index (specified in '/src/assets/default.glsl'), size of attributes
        // passing type, normalization, bytes until next vertex, starting point
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);

        logger.log("Chunk [" + chunkID + "] instantiated");
    }

    @Override
    public void update(float dt) {
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }
}
