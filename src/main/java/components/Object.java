package components;

// Not doing anything except storing object id for now
// Overall, is used to apply object modifiers when I thought of it
public abstract class Object {
    protected String objectID;

    // For culling and hiding objects
    private boolean isVisible = false;

    public void setVisibility (boolean visibility) {
        this.isVisible = visibility;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public Object(String objectID) {
        this.objectID = objectID;
    }

//    public abstract void init ();
//
//    public abstract void update(float dt);
}
