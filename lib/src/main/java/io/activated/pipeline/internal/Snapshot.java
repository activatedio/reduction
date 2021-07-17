package io.activated.pipeline.internal;

public interface Snapshot extends Renderable {

  Diff diff(Snapshot previous);
}
