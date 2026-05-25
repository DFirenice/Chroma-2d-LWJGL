package scenes;

import engine.Camera;
import engine.Window;

public abstract class Scene {
    public Scene () {}

    protected Camera camera;

    public abstract void update(float dt);

    public void init() {
        Window.get().r = 1.0f;
        Window.get().g = 1.0f;
        Window.get().b = 1.0f;
        Window.get().a = 1.0f;
    }
}
