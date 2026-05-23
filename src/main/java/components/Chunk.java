package components;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

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
    protected Vector2f posIndex;

    private float[] vertexArray;
    private int[] elementArray;

    private int vaoID, vboID, eboID;

    // Tets for a dynamic value
    private FloatBuffer vertexBuffer;
    private IntBuffer elementBuffer;

    public Chunk(String chunkID, Vector2f posIndex) {
        super(chunkID);
        this.posIndex = posIndex;

        assert posIndex.x != 0 && posIndex.y != 0 : "Error: Invalid chunk coordinates - (" + posIndex.x + ", " + posIndex.y + ")";

        this.vertexArray = new float[]{
             // 3f Vector                                                 Color                       Texture
             CHUNK_SIZE * posIndex.x, -CHUNK_SIZE * posIndex.y, 0.0f,     0.0f, 0.0f, 0.0f, 0.0f,     0.0f, 0.0f, // Bottom right
             CHUNK_SIZE * posIndex.x,  CHUNK_SIZE * posIndex.y, 0.0f,     0.0f, 0.0f, 0.0f, 0.0f,     0.0f, 0.0f, // Top Right
            -CHUNK_SIZE * posIndex.x,  CHUNK_SIZE * posIndex.y, 0.0f,     0.0f, 0.0f, 0.0f, 0.0f,     0.0f, 0.0f, // Top left
            -CHUNK_SIZE * posIndex.x, -CHUNK_SIZE * posIndex.y, 0.0f,     0.0f, 0.0f, 0.0f, 0.0f,     0.0f, 0.0f, // Bottom left
        };

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
        int positionSize = 3;   // x, y, z
        int colorSize = 4;      // r, g, b, a
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;

        // What index (specified in '/src/assets/default.glsl'), size of attributes
        // passing type, normalization, bytes until next vertex, starting point
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
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
