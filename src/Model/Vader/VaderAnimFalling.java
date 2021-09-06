package Model.Vader;

public class VaderAnimFalling implements VaderAnimStrategy {

    Vader context;

    public VaderAnimFalling(Vader context) {
        this.context = context;
    }
    @Override
    public void animate() {
        context.location.y += context.UNIT_MOVE_FALLING;

    }
}
