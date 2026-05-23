package components;

public abstract class Object {
    protected String objectID;

    public Object(String objectID) {
        this.objectID = objectID;
    }

    public abstract void init ();

    public abstract void update(float dt);
}
