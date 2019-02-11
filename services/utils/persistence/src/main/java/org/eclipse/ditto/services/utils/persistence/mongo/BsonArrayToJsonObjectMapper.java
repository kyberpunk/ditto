/*
 * Copyright (c) 2017-2018 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.services.utils.persistence.mongo;

import static org.eclipse.ditto.model.base.common.ConditionChecker.checkNotNull;

import java.util.function.Function;

import javax.annotation.concurrent.Immutable;

import org.bson.BsonArray;
import org.eclipse.ditto.json.JsonArray;

/**
 * This function maps a specified {@link BsonArray} to a {@link JsonArray}.
 * While mapping, the keys of all JSON objects can be revised by utilising a configurable function.
 */
@Immutable
final class BsonArrayToJsonObjectMapper extends AbstractBasicDBMapper<BsonArray, JsonArray> {

    private BsonArrayToJsonObjectMapper(final Function<String, String> theJsonKeyNameReviser) {
        super(theJsonKeyNameReviser);
    }

    /**
     * Returns an instance of {@code BsonArrayToJsonObjectMapper}.
     *
     * @param jsonKeyNameReviser is used to revise the json key names of JSON objects.
     * @return the instance.
     * @throws NullPointerException if {@code jsonKeyNameReviser} is {@code null}.
     */
    public static BsonArrayToJsonObjectMapper getInstance(final Function<String, String> jsonKeyNameReviser) {
        return new BsonArrayToJsonObjectMapper(jsonKeyNameReviser);
    }

    @Override
    public JsonArray apply(final BsonArray basicDBList) {
        return AbstractBasicDBMapper.mapBsonArrayToJsonArray(
                checkNotNull(basicDBList, "BsonArray to be mapped"), jsonKeyNameReviser);
    }

}