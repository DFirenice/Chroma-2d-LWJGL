package Scenes;

import utils.Logger;

public class LevelEditorScene extends Scene {
    Logger logger = new Logger(this.getClass());

    public LevelEditorScene () {
        logger.log("Swap: Scene active");
    }

    @Override
    public void update(float dt) {}
}
