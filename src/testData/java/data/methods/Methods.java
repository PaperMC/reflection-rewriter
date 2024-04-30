package data.methods;

import data.types.hierarchy.Entity;
import data.types.hierarchy.Player;

@SuppressWarnings("unused")
public class Methods {

    public Entity get() {
        return new Player();
    }

    public void consume(final Player player) {
        player.getName();
    }

    public static Entity getStatic() {
        return new Player();
    }

    public static void consumeStatic(final Player player) {
    }
}
