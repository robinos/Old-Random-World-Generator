package modelTypes;

public interface Transformable {

    public void addVector(Vector3D u);

    public void subtractVector(Vector3D u);

    public void add(Transform3D xform);

    public void subtract(Transform3D xform);

    public void addRotation(Transform3D xform);

    public void subtractRotation(Transform3D xform);
}
