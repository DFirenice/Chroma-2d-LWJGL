package scenes;

import engine.Camera;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import utils.Logger;
import utils.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    Logger logger = new Logger(this.getClass());

    private Shader defaultShader;
    private Texture testTexture;

    private int vaoID, vboID, eboID;

    // Cube vertices with local coordinates
    private float[] vertexArray = {
        // position               // color                  // texture coordinates
         100.0f, -100.0f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f,   1, 1, // bottom right
         100.0f,  100.0f, 0.0f,   0.0f, 1.0f, 0.0f, 1.0f,   1, 0, // up right
        -100.0f,  100.0f, 0.0f,   0.0f, 0.0f, 1.0f, 1.0f,   0, 0, // up left
        -100.0f, -100.0f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f,   0, 1, // bottom left
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
        testTexture = new Texture("src/assets/textures/testTexture.jpg");

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
        defaultShader.use();

        // Uploading Texture
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        // Uploading Camera matrices
        defaultShader.uploadMatrix4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMatrix4f("uView", camera.getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        defaultShader.detach();
    }
}
