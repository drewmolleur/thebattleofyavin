package Model.Missile;

public class MissileAnimShooting implements MissileAnimStrategy {

    Missile context;

    public MissileAnimShooting(Missile context) {
        this.context = context;
    }

    @Override
    public void animate() {
        double remaining = context.target.distance(context.location);
        if (remaining <= context.UNIT_MOVE) {
            // Within one step: land exactly on the target so the missile
            // explodes instead of overshooting and jittering around it forever.
            context.location.x = context.target.x;
            context.location.y = context.target.y;
            return;
        }
        double rad = Math.atan2(context.target.y - context.location.y, context.target.x - context.location.x);
        context.location.x += context.UNIT_MOVE * Math.cos(rad);
        context.location.y += context.UNIT_MOVE * Math.sin(rad);
    }
}
