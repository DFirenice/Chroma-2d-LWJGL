package scenes;

import org.lwjgl.BufferUtils;
import utils.Logger;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    Logger logger = new Logger(this.getClass());

    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexId, fragmentId, shaderProgram; // Shader program is combination of two
    private int vaoID, vboID, eboID;

    // Cube vertices
    private float[] vertexArray = {
        // position         // color
         0.5f, -0.5f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f,  // bottom right
         0.5f,  0.5f, 0.0f,   0.0f, 1.0f, 0.0f, 1.0f,  // up right
        -0.5f,  0.5f, 0.0f,   0.0f, 0.0f, 1.0f, 1.0f,  // up left
        -0.5f, -0.5f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f,  // bottom left
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
        logger.log("Swap: Scene active");
    }

    @Override
    public void init() {
        // Allocating memory for a shader (creating it basically)
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        // Setting the source & compiling the shader
        glShaderSource(vertexId, vertexShaderSrc);
        glCompileShader(vertexId);
        // Checking for compilation errors
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success == 0) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH); // Getting length because C languages require it
            logger.log("ERROR: 'default.glsl'\n\tVertex shader compilation failed.");
            logger.log(glGetShaderInfoLog(vertexId, len));
            assert false: ""; // breaking out of program
        }

        // Same process applies to fragments
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentShaderSrc);
        glCompileShader(fragmentId);
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            logger.log("ERROR: 'default.glsl'\n\tFragment shader compilation failed.");
            logger.log(glGetShaderInfoLog(fragmentId, len));
            assert false: "";
        }

        // Creating a program and attaching shaders to it
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        // Linking shaders and checking for errors
        glLinkProgram(shaderProgram);

        // Errors check
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            logger.log("ERROR: 'default.glsl'\n\tShaders linking failed.");
            logger.log(glGetProgramInfoLog(shaderProgram, len));
            assert false: "";
        }

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
        glUseProgram(shaderProgram);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
    }
}
