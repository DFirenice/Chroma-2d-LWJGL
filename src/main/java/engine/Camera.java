package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix;
    public Vector2f position;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();

        this.viewMatrix
            .translate(-position.x, -position.y, -20.0f)
            .rotateX((float)Math.toRadians(63))
            .rotateY((float)Math.toRadians(0))
            .rotateZ((float)Math.toRadians(-46));

        adjustProjection();
    }

    public void adjustProjection() {
        this.projectionMatrix.identity();
        this.projectionMatrix.ortho(
            -640.0f,
            640.0f,
            -360.0f,
            360.0f,
            -5000.0f,
            5000.0f
        );
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    // Old View matrix. Plain 2D
    //public Matrix4f getViewMatrix() {
    //    Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
    //    Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
    //
    //    this.viewMatrix.identity();
    //    // Where is the camera,
    //    // Its front,
    //    // What its up vector
    //    viewMatrix.lookAt(
    //        new Vector3f(position.x, position.y, 20.0f),
    //        cameraFront.add(position.x, position.y, 0.0f),
    //        cameraUp
    //    );
    //
    //    return this.viewMatrix;
    //}

    public Matrix4f getViewMatrix() {
        return this.viewMatrix;
    }

    public void updatePosition() {
        this.viewMatrix.identity();
        this.viewMatrix
            .translate(position.x, position.y, -20.0f)
            .rotateX((float)Math.toRadians(63))
            .rotateY((float)Math.toRadians(0))
            .rotateZ((float)Math.toRadians(-46));
    }
}
