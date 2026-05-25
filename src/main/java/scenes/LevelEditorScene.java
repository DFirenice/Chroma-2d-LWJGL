package scenes;

import components.Terrain;
import engine.Camera;
import engine.KeyListener;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import utils.Logger;
import utils.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    Logger logger = new Logger(this.getClass());

    private Shader defaultShader;
    //private Texture testTexture;

    // World
    private Terrain levelTerrain;
    private static final float CAMERA_SPEED = 250.0f;

    public LevelEditorScene () {
        defaultShader = new Shader("src/assets/shaders/default.glsl");
        logger.log("Swap: Scene active");
    }

    private void handleCameraMovement(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            camera.position.x += CAMERA_SPEED * dt;
            camera.updatePosition();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            camera.position.x -= CAMERA_SPEED * dt;
            camera.updatePosition();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            camera.position.y -= CAMERA_SPEED * dt;
            camera.updatePosition();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            camera.position.y += CAMERA_SPEED * dt;
            camera.updatePosition();
        }
    }

    @Override
    public void init() {
        // Creating perspective by having a custom camera
        this.camera = new Camera(new Vector2f());

        // Initializing & compiling shaders
        defaultShader.init();
        //testTexture = new Texture("src/assets/textures/testTexture.jpg");

        // Instantiating world
        this.levelTerrain = new Terrain();
        levelTerrain.init();
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

        // Uploading Texture
//        defaultShader.uploadTexture("TEX_SAMPLER", 0);
//        glActiveTexture(GL_TEXTURE0);
//        testTexture.bind();

        // Uploading Camera projection matrices
        defaultShader.uploadMatrix4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMatrix4f("uView", camera.getViewMatrix());

        levelTerrain.update(dt);

        // Camera movement (WASD)
        handleCameraMovement(dt);

        glBindVertexArray(0);
        defaultShader.detach();
    }
}