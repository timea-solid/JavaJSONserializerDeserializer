package com.example.person.jackson;

import com.example.person.Person;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonDeserializer extends StdDeserializer<Person> {

    public PersonDeserializer() {
        this((Class<Person>)null);
    }

    public PersonDeserializer(final Class<Person> person) {
        super(person);
    }

    @Override
    public Person deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
        final JsonNode node = jp.getCodec().readTree(jp);

        final Person person = new Person();

        if (node.get("@name") != null) {
            final String name = node.get("@name").textValue();
            person.name = name;
        }

        if (node.get("pets") != null) {
            final ArrayNode pets = (ArrayNode) node.get("pets");
            final List<String> finalPets = new ArrayList<>();
            pets.forEach(onePet -> finalPets.add(onePet.textValue()));
            person.pets = finalPets;
        }

        if (node.get("degree") != null) {
            final JsonNode degree = node.get("degree");
            final Map<String, Object> finalDegree = new HashMap<>();
            degree.fields().forEachRemaining(field -> finalDegree.put(field.getKey(), field.getValue()));
            person.degree = finalDegree;
        }

        return person;
    }
    
}
