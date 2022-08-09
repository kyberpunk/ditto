/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.connectivity.model.signals.commands.query;

import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.FieldType;
import org.eclipse.ditto.base.model.json.JsonParsableCommand;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.AbstractCommand;
import org.eclipse.ditto.connectivity.model.ConnectionId;
import org.eclipse.ditto.connectivity.model.signals.commands.ConnectivityCommand;
import org.eclipse.ditto.json.JsonArray;
import org.eclipse.ditto.json.JsonCollectors;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonValue;

/**
 * Command that retrieves several {@link org.eclipse.ditto.connectivity.model.Connection}s based on the passed in list
 * of Connection IDs.
 *
 * @since 3.0.0
 */
@Immutable
@JsonParsableCommand(typePrefix = ConnectivityCommand.TYPE_PREFIX, name = RetrieveConnections.NAME)
public final class RetrieveConnections extends AbstractCommand<RetrieveConnections>
        implements ConnectivityQueryCommand<RetrieveConnections> {

    /**
     * Name of the "Retrieve Connections" command.
     */
    public static final String NAME = "retrieveConnections";

    /**
     * Type of this command.
     */
    public static final String TYPE = ConnectivityCommand.TYPE_PREFIX + NAME;

    static final JsonFieldDefinition<JsonArray> JSON_CONNECTION_IDS =
            JsonFactory.newJsonArrayFieldDefinition("connectionIds", FieldType.REGULAR,
                    JsonSchemaVersion.V_2);
    static final JsonFieldDefinition<Boolean> JSON_IDS_ONLY =
            JsonFactory.newBooleanFieldDefinition("idsOnly", FieldType.REGULAR, JsonSchemaVersion.V_2);
    static final JsonFieldDefinition<Long> JSON_DEFAULT_TIMEOUT =
            JsonFactory.newLongFieldDefinition("timeoutMs", FieldType.REGULAR, JsonSchemaVersion.V_2);

    private final Set<ConnectionId> connectionIds;
    private final boolean idsOnly;

    private final Duration timeout;

    private RetrieveConnections(final Set<ConnectionId> connectionIds, final boolean idsOnly,
            final Duration defaultTimeout, final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        this.connectionIds = Collections.unmodifiableSet(connectionIds);
        this.idsOnly = idsOnly;
        this.timeout = dittoHeaders.getTimeout().orElse(defaultTimeout);
    }

    /**
     * Returns a new instance of the retrieve connections command.
     *
     * @param connectionIds the IDs of the connections to be retrieved.
     * @param dittoHeaders provide additional information regarding connections retrieval like a correlation ID.
     * @return the instance.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static RetrieveConnections newInstance(final Collection<ConnectionId> connectionIds,
            final boolean idsOnly, final Duration defaultTimeout, final DittoHeaders dittoHeaders) {

        return new RetrieveConnections(new LinkedHashSet<>(checkNotNull(connectionIds, "connectionIds")),
                idsOnly, defaultTimeout, dittoHeaders);
    }

    /**
     * Creates a new {@code RetrieveConnections} from a JSON string.
     *
     * @param jsonString the JSON string of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonString} is {@code null}.
     * @throws IllegalArgumentException if {@code jsonString} is empty.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonString} was not in the expected
     * format.
     */
    public static RetrieveConnections fromJson(final String jsonString, final DittoHeaders dittoHeaders) {
        return fromJson(JsonFactory.newObject(jsonString), dittoHeaders);
    }

    /**
     * Creates a new {@code RetrieveConnections} from a JSON object.
     *
     * @param jsonObject the JSON object of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static RetrieveConnections fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        final Set<ConnectionId> extractedConnectionIds = jsonObject.getValueOrThrow(JSON_CONNECTION_IDS).stream()
                .filter(JsonValue::isString)
                .map(JsonValue::asString)
                .map(ConnectionId::of)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        final boolean idsOnly = jsonObject.getValueOrThrow(JSON_IDS_ONLY);
        final Duration defaultTimeout = Duration.ofMillis(jsonObject.getValueOrThrow(JSON_DEFAULT_TIMEOUT));

        return new RetrieveConnections(extractedConnectionIds, idsOnly, defaultTimeout, dittoHeaders);
    }

    public Set<ConnectionId> getConnectionIds() {
        return connectionIds;
    }

    public boolean getIdsOnly() {
        return idsOnly;
    }

    public Duration getTimeout() {
        return timeout;
    }

    @Override
    public Category getCategory() {
        return Category.QUERY;
    }

    @Override
    public RetrieveConnections setDittoHeaders(final DittoHeaders dittoHeaders) {
        return new RetrieveConnections(connectionIds, idsOnly, timeout, dittoHeaders);
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion jsonSchemaVersion,
            final Predicate<JsonField> thePredicate) {

        final Predicate<JsonField> predicate = jsonSchemaVersion.and(thePredicate);
        final JsonArray connectionIdsArray = connectionIds.stream()
                .map(String::valueOf)
                .map(JsonFactory::newValue)
                .collect(JsonCollectors.valuesToArray());

        jsonObjectBuilder.set(JSON_CONNECTION_IDS, connectionIdsArray, predicate);
        jsonObjectBuilder.set(JSON_IDS_ONLY, idsOnly);
        jsonObjectBuilder.set(JSON_DEFAULT_TIMEOUT, timeout.toMillis());
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final RetrieveConnections that = (RetrieveConnections) o;
        return Objects.equals(connectionIds, that.connectionIds) &&
                Objects.equals(idsOnly, that.idsOnly) &&
                Objects.equals(timeout, that.timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), connectionIds, idsOnly, timeout);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", connectionIds=" + connectionIds +
                ", idsOnly=" + idsOnly +
                ", timeout=" + timeout +
                "]";
    }

}
