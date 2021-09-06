package Model.VaderBlaster;

import java.awt.*;

public class VaderBlasterAnimExploding implements VaderBlasterAnimStrategy {

    VaderBlaster context;

    public VaderBlasterAnimExploding(VaderBlaster context) {
        this.context = context;
    }

    @Override
    public void animate() {
                    context.color = Color.YELLOW;
            ++context.size;

    }
}
