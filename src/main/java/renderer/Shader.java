package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;
import utils.Logger;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader {
    private String shaderFilePath;
    private String shaderSource, vertexSource, fragmentSource;

    private int shaderProgramID, vertexID, fragmentID;
    private boolean isBeingUsed = false;

    private Logger logger = new Logger(this.getClass());

    // Obtaining shaders (fragment and vertex) as string chunks from the file
    public Shader(String glslFilePath) {
        this.shaderFilePath = glslFilePath;
        try {
            shaderSource = new String(Files.readAllBytes(Paths.get(glslFilePath))); // glsl contents
            String[] shaderSplit = shaderSource.split("(#type)( )+([a-zA-z]+)"); // type-spaces-param split
            int lastIndex = 0;

            for (String source : shaderSplit) {
                if (source.isBlank()) continue;

                int startingIdx = shaderSource.indexOf("#type", lastIndex) + 6; // #type [param]
                int eol = shaderSource.indexOf("\n", startingIdx);
                String type = shaderSource.substring(startingIdx, eol).trim();
                lastIndex = eol; // Setting pointer back to where we left off

                // Glsl doesn't need '#type [param]', hence only passing the source
                if (type.equals("vertex")) { vertexSource = source; }
                else if (type.equals("fragment")) { fragmentSource = source; }


                else { throw new IOException("Unknown type '" + type + "'"); }
            }
        }
        catch (IOException err) {
            err.getStackTrace();
            assert false: "Error reading '" + glslFilePath + "' shader";
        }
    }

    public void init() {
        // Allocating memory for a shader (creating it basically)
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Setting the source & compiling the shader
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        // Checking for compilation errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == 0) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH); // Getting length because C languages require it
            logger.log("ERROR: '" + shaderFilePath + "'\n\tVertex shader compilation failed.");
            logger.log(glGetShaderInfoLog(vertexID, len));
            assert false: ""; // breaking out of program
        }

        // Same process applies to fragments
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            logger.log("ERROR: '" + shaderFilePath + "'\n\tFragment shader compilation failed.");
            logger.log(glGetShaderInfoLog(fragmentID, len));
            assert false: "";
        }

        // Creating a program and attaching shaders to it
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        // Linking shaders and checking for errors
        glLinkProgram(shaderProgramID);

        // Errors check
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            logger.log("ERROR: '" + shaderFilePath + "'\n\tShaders linking failed.");
            logger.log(glGetProgramInfoLog(shaderProgramID, len));
            assert false: "";
        }
    }

    public void use() {
        // Checking so the program doesn't rebind
        // if already being used
        if (!isBeingUsed) {
            glUseProgram(shaderProgramID);
            isBeingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        isBeingUsed = false;
    }

    public void uploadMatrix4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use(); // Making sure context is for this shader
        // Converting the matrix to the plain [x, y, z, w, ...] array
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16); // plain 4x4 Matrix
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMatrix3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9); // plain 4x4 Matrix
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec4f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
    }

    public void uploadVec3f(String varName, Vector3f vec3f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec3f.x, vec3f.y, vec3f.z);
    }

    public void uploadVec3f(String varName, Vector2f vec2f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec2f.x, vec2f.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, slot);
    }
}
