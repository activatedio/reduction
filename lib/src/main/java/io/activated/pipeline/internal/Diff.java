package io.activated.pipeline.internal;

import java.io.IOException;
import java.io.Writer;

public interface Diff extends Renderable {

  static Diff CLEAR =
      new Diff() {
        @Override
        public void render(Writer w) throws IOException {
          w.write("<cleared>");
        }
      };
}
