package com.example.person.jsonb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.person.Person;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PersonJsonbSerializeTest {

    private static Person person;
    private static Person personCopy;
    private static Jsonb jsonb;

    @BeforeAll
    static void setup() {
        final JsonbConfig config = new JsonbConfig();
        config.withAdapters(new PersonAdapter());
        config.withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES);
        jsonb = JsonbBuilder.create(config);
    }

    @Test
    void roundtripPerson() throws IOException {
        try (final var res = PersonJsonbSerializeTest.class
                .getResourceAsStream("/person.json")) {
            person = jsonb.fromJson(res, Person.class);
        }

        final var targetPath = new File("target").toPath();
        final var testFolderName = UUID.randomUUID().toString();
        final var testFolderPath = Files.createTempDirectory(targetPath, testFolderName);
        final var testFile =
                Files.createTempFile(testFolderPath, UUID.randomUUID().toString(), ".json");

        try (final var out = new FileOutputStream(testFile.toString())) {
            jsonb.toJson(person, out);
        }

        try (final var in = new FileInputStream(testFile.toString())) {
            personCopy = jsonb.fromJson(in, Person.class);
        }

        assertEquals(person.name, personCopy.name);
        assertEquals(person.pets, personCopy.pets);
        assertEquals(person.degree, personCopy.degree);

        Files.deleteIfExists(testFile);
        Files.deleteIfExists(testFolderPath);

    }

}
