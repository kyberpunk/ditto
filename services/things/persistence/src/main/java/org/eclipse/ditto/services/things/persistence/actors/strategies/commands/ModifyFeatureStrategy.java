/*
 * Copyright (c) 2017 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 * Contributors:
 *    Bosch Software Innovations GmbH - initial contribution
 *
 */
package org.eclipse.ditto.services.things.persistence.actors.strategies.commands;

import java.util.Optional;

import javax.annotation.concurrent.ThreadSafe;

import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.things.Features;
import org.eclipse.ditto.signals.commands.things.modify.ModifyFeature;
import org.eclipse.ditto.signals.commands.things.modify.ModifyFeatureResponse;
import org.eclipse.ditto.signals.commands.things.modify.ThingModifyCommandResponse;
import org.eclipse.ditto.signals.events.things.FeatureCreated;
import org.eclipse.ditto.signals.events.things.FeatureModified;
import org.eclipse.ditto.signals.events.things.ThingModifiedEvent;

/**
 * This strategy handles the {@link org.eclipse.ditto.signals.commands.things.modify.ModifyFeature} command.
 */
@ThreadSafe
final class ModifyFeatureStrategy extends AbstractCommandStrategy<ModifyFeature> {

    /**
     * Constructs a new {@code ModifyFeatureStrategy} object.
     */
    ModifyFeatureStrategy() {
        super(ModifyFeature.class);
    }

    @Override
    protected CommandStrategy.Result doApply(final CommandStrategy.Context context, final ModifyFeature command) {
        final DittoHeaders dittoHeaders = command.getDittoHeaders();
        final ThingModifiedEvent eventToPersist;
        final ThingModifyCommandResponse response;

        final Optional<Features> features = context.getThing().getFeatures();
        if (features.isPresent() && features.get().getFeature(command.getFeatureId()).isPresent()) {
            eventToPersist = FeatureModified.of(command.getId(), command.getFeature(), context.getNextRevision(),
                    eventTimestamp(), dittoHeaders);
            response = ModifyFeatureResponse.modified(context.getThingId(), command.getFeatureId(), dittoHeaders);
        } else {
            eventToPersist = FeatureCreated.of(command.getId(), command.getFeature(), context.getNextRevision(),
                    eventTimestamp(), dittoHeaders);
            response = ModifyFeatureResponse.created(context.getThingId(), command.getFeature(), dittoHeaders);
        }

        return ImmutableResult.of(eventToPersist, response);
    }

}
