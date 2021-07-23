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
package io.activated.pipeline.micronaut.e2e.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.micronaut.e2e.domain.ToDo;
import io.activated.pipeline.micronaut.e2e.repository.ToDoRepository;
import javax.inject.Singleton;

/** @author Marcel Overdijk */
@Singleton
@SuppressWarnings("Duplicates")
public class CompleteToDoDataFetcher implements DataFetcher<Boolean> {

  private final ToDoRepository toDoRepository;

  public CompleteToDoDataFetcher(ToDoRepository toDoRepository) {
    this.toDoRepository = toDoRepository;
  }

  @Override
  public Boolean get(DataFetchingEnvironment env) {
    String id = env.getArgument("id");
    ToDo toDo = toDoRepository.findById(id);
    if (toDo != null) {
      toDo.setCompleted(true);
      toDoRepository.save(toDo);
      return true;
    } else {
      return false;
    }
  }
}
