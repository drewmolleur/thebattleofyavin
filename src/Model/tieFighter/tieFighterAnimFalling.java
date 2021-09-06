package Model.tieFighter;

public class tieFighterAnimFalling implements tieFighterAnimStrategy {

    tieFighter context;

    public tieFighterAnimFalling(tieFighter context) {
        this.context = context;
    }
    @Override
    public void animate() {
        context.location.y += context.UNIT_MOVE_FALLING;
    }
}
