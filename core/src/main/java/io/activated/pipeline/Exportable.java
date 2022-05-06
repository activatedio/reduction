package io.activated.pipeline;

/**
 * Allows a class to export a different type for public consumption
 *
 * @param <E> type to export
 */
public interface Exportable<E> {
  E export();
}
