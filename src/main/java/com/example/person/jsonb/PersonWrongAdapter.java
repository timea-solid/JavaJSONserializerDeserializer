package com.example.person.jsonb;

import com.example.person.Person;
import java.util.ArrayList;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.bind.adapter.JsonbAdapter;

public class PersonWrongAdapter implements JsonbAdapter<Person, JsonObject> {

    @Override
    public JsonObject adaptToJson(final Person person) throws Exception {

        final var result = Json.createObjectBuilder();

        if (person.name != null) {
            result.add("@name", person.name);
        }

        if (person.pets != null) {
            final var arrayBuilder = Json.createArrayBuilder();
            person.pets.forEach(arrayBuilder::add);
            result.add("pets", arrayBuilder.build());
        }

        if (person.degree != null) {
            result.add("degree", person.degree.toString());
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

}
