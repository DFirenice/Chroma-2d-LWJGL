package components;

import org.joml.Vector2f;

import java.util.ArrayList;

public class Terrain {
    private Vector2f size2f = new Vector2f(2.0f, 2.0f);
    private ArrayList<Chunk> chunks = new ArrayList<>((int)(size2f.x * size2f.y));

    // 50 units ~ 50px
    public static final float CHUNK_SIZE = 50.0f;

    public Terrain() {}

    public void load() {
        // Generating chunks for the plane
        for (int x = 1; x < chunks.size(); x++) {
            for (int y = 1; y < chunks.size(); y++) {
                chunks.add(
                    new Chunk("chunk_" + x + "_" + y, new Vector2f(x, y))
                );
            }
        }
    }

    // Updating chunks
    public void update(float dt) {
        // for each chunk, update chunk with a new deltaTime
    }
}
