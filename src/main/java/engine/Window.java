package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderer.Shader;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;
import utils.Logger;
import utils.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/*
* LWJGL docs reference:
* https://www.lwjgl.org/guide
*/

public class Window {
    // Essential window params
    public final int width, height;
    private final String title;
    private long glfwWindow;

    // Transitions
    private boolean isFading = false;

    public float r, g, b, a;

    private static Scene currentScene = null;

    private static Window window = null;
    private final Logger logger = new Logger(this.getClass());

    // Constructing the window
    private Window () {
        this.width = 1280; // 4:3, 720p
        this.height = 720;
        this.title = "Chroma";
        r = 1; g = 1; b = 1; a = 1;
    }

    // Instantiating so only one window can exist
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return window;
    }

    // Scene manager to swap world levels (scenes)
    public void swapScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false: "Unknown scene '" + newScene + "'";
                break;
        }
    }

    // Running the window using LWJGL
    public void run() {
        init();
        loop();

        // Cleanup callbacks on exit even thought the system
        // does it automatically, this is considered
        // a good practice
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setting up an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Application's window config
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // configuring before enabling
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // GUI itself, Is a memory address (type of long)
        glfwWindow = GLFW.glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            glfwTerminate();
            throw new IllegalStateException("Failed to create a GLFW window");
        }

        // Hooking up custom callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::scrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Making the OpenGL the context current
        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1); // v-sync to limit fps to the monitor

        glfwShowWindow(glfwWindow); // displaying the window

        // Idk what this does, honestly.
        // Sounded important on docs for 'bindings'
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);

        this.swapScene(0);

        logger.log("Initialized. GLFW version: " + Version.getVersion());
    }

    public void loop() {
        float initTime = Time.getTime();
        float endTime, deltaTime = 0.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {

            // Processing all important events like keyboard / mouse
            glfwPollEvents();

            // Each frame must be cleared before re-rendering
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Handling scene-swapping on 'K' key
            if (KeyListener.isKeyPressed(GLFW_KEY_K) && r == 1) {
                isFading = true;
            }

            if (isFading && currentScene.getClass().getName().equals(LevelEditorScene.class.getName())) {
                r = Math.max(r - 0.05f, 0.0f);
                g = Math.max(g - 0.05f, 0.0f);
                b = Math.max(b - 0.05f, 0.0f);
                if (r <= 0) {
                    isFading = false;
                    this.swapScene(1);
                }
            }

            currentScene.update(deltaTime);

            // Swapping buffering layers
            glfwSwapBuffers(glfwWindow);

            // Measuring execution time
            // Used as well in scenes as 'deltaTime'
            endTime = Time.getTime();
            deltaTime = endTime - initTime;
            initTime = endTime;
        }
    }
}
