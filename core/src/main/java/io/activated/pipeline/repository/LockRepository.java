package io.activated.pipeline.repository;

import io.activated.pipeline.Lock;

public interface LockRepository {

  Lock acquire(String key);

  void release(Lock lock);
}
