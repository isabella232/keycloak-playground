/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.playground.nodowntimeupgrade.base.storage;

import org.keycloak.playground.nodowntimeupgrade.base.model.HasId;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author hmlnarik
 */
public interface Storage<ModelType extends HasId<String>> {
    /**
     * Creates an object in the store. Introduces new ID.
     * @param object
     * @return ID of the object
     */
    String create(ModelType object);

    /**
     * Returns object with the given {@code id} from the storage or {@code null} if object does not exist.
     * @param id
     * @return See description
     */
    ModelType read(String id);

    /**
     * Returns criteria builder for the storage engine.
     * The criteria are specified in the given criteria builder based on model properties.
     * <br>
     * <b>Note:</b> While the criteria are formulated in terms of model properties,
     * the storage engine may in turn process them into the best form that suits the
     * underlying storage engine query language, e.g. to conditions on storage
     * attributes or REST query parameters.
     * If possible, do <i>not</i> delay filtering after the models are reconstructed from
     * storage entities, in most cases this would be highly inefficient.
     *
     * @return See description
     */
    ModelCriteriaBuilder getCriteriaBuilder();

    /**
     * Returns stream of objects satisfying given {@code criteria} from the storage.
     * The criteria are specified in the given criteria builder based on model properties.
     * 
     * @param criteria
     * @return Stream of objects. Never returns {@code null}.
     */
    Stream<ModelType> read(ModelCriteriaBuilder criteria);

    /**
     * Writes object with the given {@code id} to the storage.
     * @param id
     * @throws NullPointerException if object or its {@code id} is {@code null}
     */
    void write(ModelType object);

    /**
     * Deletes object with the given {@code id} from the storage, if exists, no-op otherwise.
     * @param id
     */
    void delete(String id);

    /**
     * Returns set of keys of objects currently present in the store.
     * @return
     */
    Set<String> keys();

    /**
     * Dumps a human-readable representation of the entity in the physical store with given {@code id}.
     * @param id ID of the object
     */
    String dump(String id);
}
