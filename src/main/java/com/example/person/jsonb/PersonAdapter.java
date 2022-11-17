package com.example.person.jsonb;

import com.example.person.Person;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.adapter.JsonbAdapter;

public class PersonAdapter implements JsonbAdapter<Person, JsonObject> {

    @Override
    public JsonObject adaptToJson(final Person person) throws Exception {

        final var result = Json.createObjectBuilder();

        if (person.name != null) {
            result.add("@name", person.name);
        }

        if (person.pets != null) {
            final var arrayBuilder = Json.createArrayBuilder();
            person.pets.forEach(onePet -> arrayBuilder.add(onePet));
            result.add("pets", arrayBuilder.build());
        }

        if (person.degree != null) {
            final var itCSubject = person.degree.entrySet().iterator();
            final var objectBuilder = Json.createObjectBuilder();
            while (itCSubject.hasNext()) {
                addRightJsonType(objectBuilder, itCSubject.next());
            }
            result.add("degree", objectBuilder.build());
        }

        return result.build();
    }

    @Override
    public Person adaptFromJson(final JsonObject adapted) throws Exception {
        final var person = new Person();

        if (adapted.containsKey("@name")) {
            person.name = adapted.getString("@name");
        }

        if (adapted.containsKey("pets")) {
            final var jsonArrayType = adapted.getJsonArray("pets");
            person.pets = new ArrayList<String>();
            jsonArrayType.forEach(value -> person.pets.add(((JsonString) value).getString()));
        }

        if (adapted.containsKey("degree")) {
            person.degree = new HashMap<String, Object>();
            final var jsonObject = adapted.getJsonObject("degree");
            person.degree.putAll(jsonObject);
        }

        return person;
    }

    private <T> void addRightJsonType(final JsonObjectBuilder objectBuilder, final Entry<String, T> entry) {
        if (entry.getValue() instanceof JsonString) {
            objectBuilder.add(entry.getKey(), (JsonString) entry.getValue());
        }
        if (entry.getValue() instanceof JsonNumber) {
            objectBuilder.add(entry.getKey(), (JsonNumber) entry.getValue());
        }
        if (entry.getValue() instanceof JsonArray) {
            final var arrayBuilder = Json.createArrayBuilder();
            ((JsonArray) entry.getValue()).forEach(oneValue -> arrayBuilder.add(oneValue));
            final var jsonArray = arrayBuilder.build();
            objectBuilder.add(entry.getKey(), jsonArray);
        }
        if (entry.getValue() instanceof JsonValue) {
            objectBuilder.add(entry.getKey(), (JsonValue) entry.getValue());
        }
        if (entry.getValue() instanceof JsonObject) {
            final var object = Json.createObjectBuilder();
            final var it = ((JsonObject) entry.getValue()).entrySet().iterator();
            while (it.hasNext()) {
                addRightJsonType(object, it.next());
            }
            objectBuilder.add(entry.getKey(), object);
        }
    }

}
