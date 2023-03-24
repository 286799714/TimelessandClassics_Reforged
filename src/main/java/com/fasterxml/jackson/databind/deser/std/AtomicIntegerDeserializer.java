package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.type.LogicalType;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

// @since 2.12
public class AtomicIntegerDeserializer extends StdScalarDeserializer<AtomicInteger>
{
    private static final long serialVersionUID = 1L;

    public AtomicIntegerDeserializer() { super(AtomicInteger.class); }

    @Override
    public AtomicInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.isExpectedNumberIntToken()) {
            return new AtomicInteger(p.getIntValue());
        }
        // 12-Jun-2020, tatu: May look convoluted, but need to work correctly with
        //   CoercionConfig
        Integer I = _parseInteger(p, ctxt, AtomicInteger.class);
        return (I == null) ? null : new AtomicInteger(I.intValue());
    }

    @Override
    public LogicalType logicalType() { return LogicalType.Integer; }

    @Override // @since 2.12
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return new AtomicInteger();
    }
}
