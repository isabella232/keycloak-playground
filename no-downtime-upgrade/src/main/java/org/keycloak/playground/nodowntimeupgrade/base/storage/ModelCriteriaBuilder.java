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

/**
 * Builder for criteria that can be used to limit results obtained from the store.
 * This class is used for similar purpose as e.g. JPA's {@code CriteriaBuilder},
 * however it is much simpler version as it is tailored to very specific needs
 * of future Keycloak store.
 * <p>
 * Implementations are expected to be immutable. The expected use is like this:
 * <pre>
 * cb = storage.getCriteriaBuilder();
 * storage.read(
 *   cb.or(
 *     cb.compare(FIELD1, EQ, 1).compare(FIELD2, EQ, 2),
 *     cb.compare(FIELD1, EQ, 3).compare(FIELD2, EQ, 4)
 *   )
 * );
 * </pre>
 * The above code should read items where
 * {@code (FIELD1 == 1 && FIELD2 == 2) || (FIELD1 == 3 && FIELD2 == 4)}.
 *
 * <p>
 * It is equivalent to this:
 * <pre>
 * cb = storage.getCriteriaBuilder();
 * storage.read(
 *   cb.or(
 *     cb.and(cb.compare(FIELD1, EQ, 1), cb.compare(FIELD2, EQ, 2)),
 *     cb.and(cb.compare(FIELD1, EQ, 3), cb.compare(FIELD2, EQ, 4))
 *   )
 * );
 * </pre>
 *
 * @author hmlnarik
 */
public interface ModelCriteriaBuilder {

    /**
     * The operators are very basic ones for this use case. In the real scenario,
     * new operators can be added, possibly with different arity, e.g. {@code IN}.
     * The {@link ModelCriteriaBuilder#compare} method would need an adjustment
     * then, likely to taky vararg {@code value} instead of single value as it
     * is now.
     */
    public enum Operator {
        EQ, NE, LT, LE, GT, GE, LIKE, ILIKE
    }

    /**
     * Adds a constraint for the given model field to this criteria builder
     * and returns a criteria builder that is combined with the the new constraint.
     * The resulting constraint is a logical conjunction (i.e. AND) of the original
     * constraint present in this {@link ModelCriteriaBuilder} and the given operator.
     *
     * @param modelField Field on the logical <i>model</i> to be constrained
     * @param op Operator
     * @param value Operand to the operator.
     * @return
     */
    ModelCriteriaBuilder compare(String modelField, Operator op, Object value);

    /**
     * Creates and returns a new instance of {@code ModelCriteriaBuilder} that
     * combines the given builders with the Boolean AND operator.
     * <p>
     * Predicate coming out of {@code and} on an empty array of {@code builders}
     * (i.e. empty conjunction) is always {@code true}.
     *
     * <pre>
     *   cb = storage.getCriteriaBuilder();
     *   storage.read(cb.or(
     *     cb.and(cb.compare(FIELD1, EQ, 1), cb.compare(FIELD2, EQ, 2)),
     *     cb.and(cb.compare(FIELD1, EQ, 3), cb.compare(FIELD2, EQ, 4))
     *   );
     * </pre>
     *
     */
    ModelCriteriaBuilder and(ModelCriteriaBuilder... builders);

    /**
     * Creates and returns a new instance of {@code ModelCriteriaBuilder} that
     * combines the given builders with the Boolean OR operator.
     * <p>
     * Predicate coming out of {@code and} on an empty array of {@code builders}
     * (i.e. empty disjunction) is always {@code false}.
     *
     * <pre>
     *   cb = storage.getCriteriaBuilder();
     *   storage.read(cb.or(
     *     cb.compare(FIELD1, EQ, 1).compare(FIELD2, EQ, 2),
     *     cb.compare(FIELD1, EQ, 3).compare(FIELD2, EQ, 4)
     *   );
     * </pre>
     */
    ModelCriteriaBuilder or(ModelCriteriaBuilder... builders);

    /**
     * Creates and returns a new instance of {@code ModelCriteriaBuilder} that
     * negates the given builder.
     * <p>
     * Note that if the {@code builder} has no condition yet, there is nothing
     * to negate: empty negation is always {@code true}.
     *
     * @param builder
     * @return
     */
    ModelCriteriaBuilder not(ModelCriteriaBuilder builder);

    /**
     * Returns this object cast to the given class.
     * @param <T>
     * @param clazz
     * @return
     * @throws ClassCastException When this instance cannot be converted to the given {@code clazz}.
     */
    <T extends ModelCriteriaBuilder> T unwrap(Class<T> clazz);

}
