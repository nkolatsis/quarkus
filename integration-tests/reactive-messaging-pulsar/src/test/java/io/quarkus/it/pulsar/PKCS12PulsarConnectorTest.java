package io.quarkus.it.pulsar;

import static io.restassured.RestAssured.get;
import static org.awaitility.Awaitility.await;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

@WithTestResource(value = PulsarResource.class, initArgs = {
        @ResourceArg(name = "isPem", value = "true"),
        @ResourceArg(name = "keyStorePassword", value = "Z_pkTh9xgZovK4t34cGB2o6afT4zZg0L"),
        @ResourceArg(name = "trustStorePassword", value = "Z_pkTh9xgZovK4t34cGB2o6afT4zZg0L"),
        @ResourceArg(name = "pulsar.tls-configuration-name", value = "custom-p12"),
}, restrictToAnnotatedClass = true)
@QuarkusTest
public class PKCS12PulsarConnectorTest {

    protected static final TypeRef<List<String>> TYPE_REF = new TypeRef<List<String>>() {
    };

    @Test
    public void testFruits() {
        await().untilAsserted(() -> Assertions.assertEquals(get("/pulsar/fruits").as(TYPE_REF).size(), 4));
    }

}
