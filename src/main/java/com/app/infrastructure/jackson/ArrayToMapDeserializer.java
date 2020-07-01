package com.app.infrastructure.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArrayToMapDeserializer extends JsonDeserializer<Map<Object, Object>>
        implements ContextualDeserializer {

    private Class<?> keyAs;

    private Class<?> contentAs;

    @Override
    public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        return this.deserialize(p, ctxt, new HashMap<>());
    }

    @Override
    public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt,
                                           Map<Object, Object> intoValue) throws IOException, JsonProcessingException {
        JsonNode node = p.readValueAsTree();
        ObjectCodec codec = p.getCodec();
        if (node.isArray()) {
            node.forEach(entry -> {
                try {
                    JsonNode keyNode = entry.get(0);
                    JsonNode valueNode = entry.get(1);
                    intoValue.put(keyNode.traverse(codec).readValueAs(this.keyAs),
                            valueNode.traverse(codec).readValueAs(this.contentAs));
                } catch (NullPointerException | IOException e) {
                    // skip entry
                }
            });
        }
        return intoValue;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property)
       {
        JsonDeserialize jsonDeserialize = property.getAnnotation(JsonDeserialize.class);
        this.keyAs = jsonDeserialize.keyAs();
        this.contentAs = jsonDeserialize.contentAs();
        return this;
    }

}
