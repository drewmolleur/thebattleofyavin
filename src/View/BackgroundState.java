package View;

import java.awt.*;

public interface BackgroundState {

    void render(Graphics2D g2);

    void goNext(MyCanvas canvas);
}
