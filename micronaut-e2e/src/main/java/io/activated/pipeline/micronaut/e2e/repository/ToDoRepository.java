/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.activated.pipeline.micronaut.e2e.repository;

import io.activated.pipeline.micronaut.e2e.domain.ToDo;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Singleton;

/** @author Marcel Overdijk */
@Singleton
@SuppressWarnings("Duplicates")
public class ToDoRepository {

  private final Map<String, ToDo> toDos = new LinkedHashMap<>();

  public ToDoRepository(AuthorRepository authorRepository) {
    save(
        new ToDo("Book flights to Gran Canaria", authorRepository.findOrCreate("William").getId()));
    save(
        new ToDo("Order torrefacto coffee beans", authorRepository.findOrCreate("George").getId()));
    save(new ToDo("Watch La Casa de Papel", authorRepository.findOrCreate("George").getId()));
  }

  public Iterable<ToDo> findAll() {
    return toDos.values();
  }

  public ToDo findById(String id) {
    return toDos.get(id);
  }

  public ToDo save(ToDo toDo) {
    if (toDo.getId() == null) {
      toDo.setId(UUID.randomUUID().toString());
    }
    toDos.put(toDo.getId(), toDo);
    return toDo;
  }

  public void deleteById(String id) {
    toDos.remove(id);
  }
}
