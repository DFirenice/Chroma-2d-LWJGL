package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix;
    public Vector2f position;

    private float CLIPPING_BUFFER = 50.0f;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();

        this.viewMatrix
            .translate(-position.x, -position.y, -20.0f)
            .rotateX((float)Math.toRadians(60))
            .rotateY((float)Math.toRadians(0))
            .rotateZ((float)Math.toRadians(-45));

        adjustProjection();
    }

    public void adjustProjection() {
        this.projectionMatrix.identity();
        this.projectionMatrix.ortho(
            -Window.get().width / 2,
            Window.get().width / 2,
            (float) -Window.get().height / 2,
            (float) Window.get().height / 2,
            -Window.get().height + CLIPPING_BUFFER,
            Window.get().height + CLIPPING_BUFFER
        );
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return this.viewMatrix;
    }

    public void updatePosition() {
        adjustProjection();
        this.viewMatrix.identity();
        this.viewMatrix
            .rotateX((float)Math.toRadians(60))
            .rotateY((float)Math.toRadians(0))
            .rotateZ((float)Math.toRadians(-45))
            .translate(-position.x, -position.y, -20.0f);
    }
}
