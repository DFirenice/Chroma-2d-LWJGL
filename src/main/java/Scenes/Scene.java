package Scenes;

import engine.Window;

public abstract class Scene {
    public Scene () {}

    public abstract void update(float dt);

    public void init() {
        Window.get().r = 1;
        Window.get().g = 1;
        Window.get().b = 1;
        Window.get().a = 1;
    }
}
