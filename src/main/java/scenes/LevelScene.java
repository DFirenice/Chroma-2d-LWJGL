package scenes;

import engine.Camera;
import utils.Logger;

public class LevelScene extends Scene {
    Logger logger = new Logger(this.getClass());

    public LevelScene() {
        logger.log("Swap: Scene active");
    }

    @Override
    public void update(float dt) {}
}
