package io.activated.pipeline.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;
import io.activated.pipeline.Mapper;
import java.io.IOException;
import java.io.Writer;

public class SnapshotterImpl implements Snapshotter {

  public static class DiffImpl implements Diff {

    private final JsonNode jsonNode;

    DiffImpl(JsonNode jsonNode) {
      this.jsonNode = jsonNode;
    }

    @Override
    public void render(Writer w) throws IOException {
      Mapper.OBJECT_MAPPER.writeValue(w, jsonNode);
    }
  }

  public static class SnapshotImpl implements Snapshot {

    private final JsonNode jsonNode;

    SnapshotImpl(JsonNode jsonNode) {
      this.jsonNode = jsonNode;
    }

    @Override
    public Diff diff(Snapshot previous) {
      var previousNode = ((SnapshotImpl) previous).jsonNode;
      return new DiffImpl(JsonDiff.asJson(previousNode, jsonNode));
    }

    @Override
    public void render(Writer w) throws IOException {
      Mapper.OBJECT_MAPPER.writeValue(w, jsonNode);
    }
  }

  @Override
  public Snapshot snapshot(Object source) {

    return new SnapshotImpl(Mapper.OBJECT_MAPPER.valueToTree(source));
  }
}
