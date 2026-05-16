package scenes;

import engine.Camera;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import utils.Logger;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    Logger logger = new Logger(this.getClass());

    private Shader defaultShader;
    private int vaoID, vboID, eboID;

    // Cube vertices
    private float[] vertexArray = {
        // position         // color
         100.5f,    0.5f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f,  // bottom right
         0.5f,    100.5f, 0.0f,   0.0f, 1.0f, 0.0f, 1.0f,  // up right
         100.5f,  100.5f, 0.0f,   0.0f, 0.0f, 1.0f, 1.0f,  // up left
         0.5f,      0.5f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f,  // bottom left
    };

    // Points must be counter-clockwise order, from bottom right
    private int[] elementArray = {
        /* in triangles:
        *
        *   3        2
        *
        *   x        1
        *
        *       or
        *
        *   2        x
        *
        *   3        1
        *
        */
        0, 1, 2, // Top right triangle
        0, 2, 3
    };

    public LevelEditorScene () {
        defaultShader = new Shader("src/assets/shaders/default.glsl");
        logger.log("Swap: Scene active");
    }

    @Override
    public void init() {
        // Creating perspective by having a custom camera
        this.camera = new Camera(new Vector2f());

        // Initializing & compiling shaders
        defaultShader.init();

        // Creating VAO, VBO, and EBO objects, and sending to the GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

            // OpenGL expects buffer, so we create buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray);
        vertexBuffer.flip(); // Finalizing the buffer for reading

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

            // Creating indices and uploading
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray);
        elementBuffer.flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Adding vertex attribute pointers
        // (basically size in memory of one stride vertex element)
        int positionSize = 3;   // x, y, z
        int colorSize = 4;      // r, g, b, a
        int floatSizeBytes = 4; // WHERE IS THE 'sizeof' like in C++, ARGHHH!
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;

        // What index (specified in '/src/assets/default.glsl'), size of attributes
        // passing type, normalization, bytes until next vertex, starting point
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        this.camera.position.x -= 32f * dt;

        defaultShader.use();
        defaultShader.uploadMatrix4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMatrix4f("uView", camera.getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        defaultShader.detach();
    }
}
