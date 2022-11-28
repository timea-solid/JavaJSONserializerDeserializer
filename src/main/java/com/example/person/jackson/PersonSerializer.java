package com.example.person.jackson;

import com.example.person.Person;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PersonSerializer extends StdSerializer<Person> {

    public PersonSerializer() {
        this((Class<Person>)null);
    }

    public PersonSerializer(final Class<Person> person) {
        super(person);
    }

    @Override
    public void serialize(final Person person, final JsonGenerator jgen,
            final SerializerProvider provider) throws IOException {

        jgen.writeStartObject();

        if (person.name != null) {
            jgen.writeStringField("@name", person.name);
        }

        if (person.pets != null) {
            jgen.writeFieldName("pets");
            jgen.writeStartArray();
            for (final var pet : person.pets) {
                jgen.writeString(pet);
            }
            jgen.writeEndArray();
        }

        if (person.degree != null) {
            jgen.writeFieldName("degree");
            jgen.writeStartObject();
            for (final var itCStatus : person.degree.entrySet()) {
                jgen.writePOJOField(itCStatus.getKey(), itCStatus.getValue());
            }
            jgen.writeEndObject();
        }

        jgen.writeEndObject();

    }

}
