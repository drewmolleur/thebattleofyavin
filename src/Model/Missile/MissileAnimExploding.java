package Model.Missile;

import java.awt.*;

public class MissileAnimExploding implements MissileAnimStrategy {

    Missile context;

    public MissileAnimExploding(Missile context) {
        this.context = context;
    }

    @Override
    public void animate() {
        context.color1 = Color.ORANGE;
        ++context.size;
    }
}
