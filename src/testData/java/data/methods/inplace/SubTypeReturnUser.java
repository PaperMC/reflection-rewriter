package data.methods.inplace;

import data.methods.Methods;
import data.types.hierarchy.Player;

@SuppressWarnings("unused")
public final class SubTypeReturnUser {
    public static void run() {
        final Methods methods = new Methods();
        final Player player = methods.get();
        final Player player2 = Methods.getStatic();
    }
}