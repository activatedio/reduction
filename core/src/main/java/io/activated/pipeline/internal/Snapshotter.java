package io.activated.pipeline.internal;

public interface Snapshotter {

  Snapshot snapshot(Object source);
}
