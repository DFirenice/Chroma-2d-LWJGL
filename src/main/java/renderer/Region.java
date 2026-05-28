package renderer;

import components.Chunk;
import org.lwjgl.BufferUtils;
import utils.Logger;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
* Batching is a process that allows to combine or and merge multiple repeating elements into
* a single mesh. This is done on purpose to not overload the draw stack,
* leading into a significant performance increase.

* Therefore, Region combines all chunks within a single Terrain
* to make them a uniform mesh sent to the GPU by one callback.
* */

public class Region {
    private final Logger logger = new Logger(this.getClass()); // dev-only

    // Array of Chunks, for all necessary chunks to be
    // later normalized into a single buffer array
    public ArrayList<Chunk> visibleChunks = new ArrayList<>();

    // Pure point data
    private FloatBuffer combinedVertices;
    private IntBuffer combinedIndices;

    private int vaoID, vboID, eboID;

    // Indexes
    private static final int POS_SIZE = 3;
    private static final int COLOR_SIZE = 4;
    private static final int UV_SIZE = 2; // Texture cords: (x,y)
    private static final int VERTEX_SIZE = (POS_SIZE + COLOR_SIZE + UV_SIZE);

    public void init() {
        // Preparing buffers
        uniformTerrainChunks();

        // Buffers to interact with the GPU
        FloatBuffer vertexBuffer;
        IntBuffer elementBuffer;

        // Creating VAO, VBO, and EBO objects, and sending to the GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // OpenGL expects buffer, so we create needed buffers
        vertexBuffer = BufferUtils.createFloatBuffer(combinedVertices.limit());
        vertexBuffer.put(combinedVertices);
        vertexBuffer.flip(); // Finalizing the buffer for reading

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Creating indices and uploading
        elementBuffer = BufferUtils.createIntBuffer(combinedIndices.limit());
        elementBuffer.put(combinedIndices);
        elementBuffer.flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Adding vertex attribute pointers
        // (basically size in memory of one stride vertex element)
        int vertexSizeBytes = VERTEX_SIZE * Float.BYTES;

        // What index (specified in '/src/assets/default.glsl'), size of attributes
        // passing type, normalization, bytes until next vertex, starting point
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, vertexSizeBytes, POS_SIZE * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, UV_SIZE, GL_FLOAT, false, vertexSizeBytes, (POS_SIZE + COLOR_SIZE) * Float.BYTES);
        glEnableVertexAttribArray(2);

        logger.log("Terrain Plane with [" + visibleChunks.size() + "] chunks has been instantiated");
    }

    // Adds chunks to the batcher
    public void batchChunk(Chunk chunk) {
        this.visibleChunks.add(chunk);
    }

    // Normalizes chunks, to construct a uniform mesh
    // (The actual batching)
    private void uniformTerrainChunks() {
        // We eventually need to know how large each buffer must be
        int totalVertices = 0;
        int totalIndices = 0;

        // Calculating total size
        for (Chunk chunk : visibleChunks) {
            totalVertices += chunk.vertexArray.length;
            totalIndices += chunk.elementArray.length;
        }

        // Allocating combined buffers
        combinedVertices = BufferUtils.createFloatBuffer(totalVertices);
        combinedIndices = BufferUtils.createIntBuffer(totalIndices);

        int vertexOffset = 0; // how many vertices we've already added

        for (Chunk chunk : visibleChunks) {

            // Adding vertices
            combinedVertices.put(chunk.vertexArray);

            // Adding indices with offset
            for (int index : chunk.elementArray) {
                combinedIndices.put(index + vertexOffset);
            }

            // Each vertex = POS + COLOR + UV = 3 + 4 + 2 = 9 floats per vertex
            int verticesPerChunk = chunk.vertexArray.length / VERTEX_SIZE;
            vertexOffset += verticesPerChunk;
        }

        combinedVertices.flip();
        combinedIndices.flip();
    }

    public void render(float dt) {
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, combinedIndices.limit(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }
}
