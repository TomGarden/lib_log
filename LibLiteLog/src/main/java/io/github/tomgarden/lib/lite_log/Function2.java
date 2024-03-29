package io.github.tomgarden.lib.lite_log;

/**
 * A function that takes 2 arguments.
 */
public interface Function2<P1, P2, R> {
    /**
     * Invokes the function with the specified arguments.
     */
    R invoke(P1 p1, P2 p2);
}