/*
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.connectivity.service.messaging.httppush;

import java.time.Duration;

import org.eclipse.ditto.connectivity.model.HmacCredentials;
import org.eclipse.ditto.connectivity.service.messaging.AzSaslRequestSigning;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;

import akka.actor.ActorSystem;

/**
 * Creator of the signing process for Azure SASL.
 */
public final class AzSaslRequestSigningFactory implements RequestSigningFactory {

    private final Duration DEFAULT_TTL = Duration.ofMinutes(15);

    @Override
    public RequestSigning create(final ActorSystem actorSystem, final HmacCredentials credentials) {
        final JsonObject parameters = credentials.getParameters();
        final String sharedKeyName = parameters.getValueOrThrow(JsonFields.SHARED_KEY_NAME);
        final String sharedKey = parameters.getValueOrThrow(JsonFields.SHARED_KEY);
        final Duration ttl = parameters.getValue(JsonFields.TTL).map(Duration::parse).orElse(DEFAULT_TTL);
        final String sr = parameters.getValue(JsonFields.SR).orElse(null);
        return AzSaslRequestSigning.of(sharedKeyName, sharedKey, ttl, sr);
    }

    /**
     * JSON fields of algorithm parameters.
     */
    public static final class JsonFields {

        /**
         * Obligatory: The shared key name.
         */
        public static JsonFieldDefinition<String> SHARED_KEY_NAME = JsonFieldDefinition.ofString("sharedKeyName");

        /**
         * Obligatory: The shared key.
         */
        public static JsonFieldDefinition<String> SHARED_KEY = JsonFieldDefinition.ofString("sharedKey");

        /**
         * Optional: How long should tokens remain valid after creation. Default to 15 minutes.
         */
        public static JsonFieldDefinition<String> TTL = JsonFieldDefinition.ofString("ttl");

        /**
         * Optional: Resource URI to include in the signature. Default to the full URI of the connection.
         * Each Azure service may use a different SR; IoT Hub uses the hostname for example.
         */
        public static JsonFieldDefinition<String> SR = JsonFieldDefinition.ofString("sr");
    }
}
