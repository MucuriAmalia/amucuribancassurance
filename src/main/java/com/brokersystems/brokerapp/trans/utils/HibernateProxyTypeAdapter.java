package com.brokersystems.brokerapp.trans.utils;

import com.google.gson.*;
import org.hibernate.proxy.HibernateProxy;
import java.lang.reflect.Type;

public class HibernateProxyTypeAdapter implements JsonSerializer<HibernateProxy>, JsonDeserializer<HibernateProxy> {
    @Override
    public JsonElement serialize(HibernateProxy src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getHibernateLazyInitializer().getImplementation());
    }

    @Override
    public HibernateProxy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        throw new JsonParseException("Deserializing Hibernate proxies is not supported.");
    }
}
