package components;

// Not doing anything except storing object id for now
// Overall, is used to apply object modifiers when I thought of it
public abstract class Object {
    protected String objectID;

    public Object(String objectID) {
        this.objectID = objectID;
    }

//    public abstract void init ();
//
//    public abstract void update(float dt);
}
