package com.example.person.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.person.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PersonWrongSerializerDeserializerTest {

    private static ObjectMapper mapper;
    private Person person;
    private Person personCopy;

    @BeforeAll
    static void setup() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        final SimpleModule simpleDeserializer = new SimpleModule();
        simpleDeserializer.addDeserializer(Person.class, new PersonDeserializer());
        mapper.registerModule(simpleDeserializer);
        final SimpleModule simpleSerializer = new SimpleModule();
        simpleSerializer.addSerializer(Person.class, new PersonWrongSerializer());
        mapper.registerModule(simpleSerializer);
    }

    @Test
    void roundtripPerson() throws IOException {
        try (final var res = PersonWrongSerializerDeserializerTest.class
                .getResourceAsStream("/person.json")) {
            person = mapper.readValue(res, Person.class);
        }

        final var targetPath = new File("target").toPath();
        final var testFolderName = UUID.randomUUID().toString();
        final var testFolderPath = Files.createTempDirectory(targetPath, testFolderName);
        final var testFile =
                Files.createTempFile(testFolderPath, UUID.randomUUID().toString(), ".json");

        try (final var out = new FileOutputStream(testFile.toString())) {
            mapper.writeValue(out, person);
        }

        try (final var in = new FileInputStream(testFile.toString())) {
            personCopy = mapper.readValue(in, Person.class);
        }

        assertEquals(person.name, personCopy.name);
        assertEquals(person.pets, personCopy.pets);
        assertEquals(person.degree, personCopy.degree);

        Files.deleteIfExists(testFile);
        Files.deleteIfExists(testFolderPath);

    }
    
}
