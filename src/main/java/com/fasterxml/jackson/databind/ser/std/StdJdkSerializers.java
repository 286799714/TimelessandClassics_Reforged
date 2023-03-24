package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.BasicSerializerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class that providers access to serializers user for non-structured JDK types that
 * are serializer as scalars; some using basic {@link ToStringSerializer},
 * others explicit serializers.
 */
@SuppressWarnings("serial")
public class StdJdkSerializers
{
    /**
     * Method called by {@link BasicSerializerFactory} to access
     * all serializers this class provides.
     */
    public static Collection<Map.Entry<Class<?>, Object>> all()
    {
        HashMap<Class<?>,Object> sers = new HashMap<Class<?>,Object>();

        // First things that 'toString()' can handle
        sers.put(java.net.URL.class, new ToStringSerializer(java.net.URL.class));
        sers.put(java.net.URI.class, new ToStringSerializer(java.net.URI.class));

        sers.put(Currency.class, new ToStringSerializer(Currency.class));
        sers.put(UUID.class, new UUIDSerializer());
        sers.put(java.util.regex.Pattern.class, new ToStringSerializer(java.util.regex.Pattern.class));
        sers.put(Locale.class, new ToStringSerializer(Locale.class));

        // then atomic types (note: AtomicReference defined elsewhere)
        sers.put(AtomicBoolean.class, AtomicBooleanSerializer.class);
        sers.put(AtomicInteger.class, AtomicIntegerSerializer.class);
        sers.put(AtomicLong.class, AtomicLongSerializer.class);

        // then other types that need specialized serializers
        sers.put(File.class, FileSerializer.class);
        sers.put(Class.class, ClassSerializer.class);

        // And then some stranger types... not 100% they are needed but:
        sers.put(Void.class, NullSerializer.instance);
        sers.put(Void.TYPE, NullSerializer.instance);

        return sers.entrySet();
    }

    /*
    /**********************************************************
    /* Serializers for atomic types
    /**********************************************************
     */

    public static class AtomicBooleanSerializer
        extends StdScalarSerializer<AtomicBoolean>
    {
        public AtomicBooleanSerializer() { super(AtomicBoolean.class, false); }
    
        @Override
        public void serialize(AtomicBoolean value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeBoolean(value.get());
        }
    
        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("boolean", true);
        }
        
        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitor.expectBooleanFormat(typeHint);
        }
    }
    
    public static class AtomicIntegerSerializer
        extends StdScalarSerializer<AtomicInteger>
    {
        public AtomicIntegerSerializer() { super(AtomicInteger.class, false); }
    
        @Override
        public void serialize(AtomicInteger value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNumber(value.get());
        }
    
        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("integer", true);
        }
        
        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
        {
            visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
        }
    }

    public static class AtomicLongSerializer
        extends StdScalarSerializer<AtomicLong>
    {
        public AtomicLongSerializer() { super(AtomicLong.class, false); }
    
        @Override
        public void serialize(AtomicLong value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNumber(value.get());
        }
    
        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("integer", true);
        }
        
        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException
        {
            visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG);
        }
    }
}
