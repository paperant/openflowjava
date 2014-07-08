/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.nx.deserialization;

import io.netty.buffer.ByteBuf;

import org.opendaylight.openflowjava.protocol.api.extensibility.HeaderDeserializer;
import org.opendaylight.openflowjava.protocol.api.extensibility.OFDeserializer;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterIdAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.augments.rev131002.ExperimenterIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.actions.grouping.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.ExperimenterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.nx.resubmit.action.rev130731.NxActionResubmitAugment;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.nx.resubmit.action.rev130731.NxActionResubmitAugmentBuilder;

/**
 * @author michal.polkorab
 *
 */
public class NxActionResubmitDeserializer implements OFDeserializer<Action>,
        HeaderDeserializer<Action> {

    @Override
    public Action deserializeHeader(ByteBuf input) {
        ActionBuilder builder = processHeader(input);
        return builder.build();
    }

    @Override
    public Action deserialize(ByteBuf input) {
        ActionBuilder builder = processHeader(input);
        NxActionResubmitAugmentBuilder nxBuilder = new NxActionResubmitAugmentBuilder();
        nxBuilder.setSubtype(input.readUnsignedShort());
        nxBuilder.setInPort(input.readUnsignedShort());
        nxBuilder.setTable(input.readUnsignedByte());
        input.skipBytes(3);
        builder.addAugmentation(NxActionResubmitAugment.class, nxBuilder.build());
        return builder.build();
    }

    private static ActionBuilder processHeader(ByteBuf input) {
        ActionBuilder builder = new ActionBuilder();
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        builder.setType(Experimenter.class);
        input.skipBytes(EncodeConstants.SIZE_OF_SHORT_IN_BYTES);
        // ExperimenterIdAction augmentation is used for vendor / experimenter differentiation
        ExperimenterIdActionBuilder expIdBuilder = new ExperimenterIdActionBuilder();
        expIdBuilder.setExperimenter(new ExperimenterId(input.readUnsignedInt()));
        builder.addAugmentation(ExperimenterIdAction.class, expIdBuilder.build());
        return builder;
    }
}