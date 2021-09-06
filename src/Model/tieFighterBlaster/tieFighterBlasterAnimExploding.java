package Model.tieFighterBlaster;

import java.awt.*;

public class tieFighterBlasterAnimExploding implements tieFighterBlasterAnimStrategy {

    tieFighterBlaster context;

    public tieFighterBlasterAnimExploding(tieFighterBlaster context) {
        this.context = context;
    }

    @Override
    public void animate() {
                    context.color = Color.YELLOW;
            ++context.size;

    }
}
