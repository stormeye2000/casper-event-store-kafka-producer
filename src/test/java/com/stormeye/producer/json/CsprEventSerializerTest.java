package com.stormeye.producer.json;

import com.casper.sdk.model.event.Event;
import com.casper.sdk.model.event.EventTarget;
import com.casper.sdk.model.event.EventType;
import com.casper.sdk.service.EventService;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author ian@meywood.com
 */
class CsprEventSerializerTest {

    @Test
    void buildKafkaEvent() {

        final String source = "http://localhost:9999";
        final String dataTxt = "data:{\"BlockAdded\":{\"block_hash\":\"bb878bcf8827649f070c487800a95c35be3eb2e83b5447921675040cea38af1c\",\"block\":{\"hash\":\"bb878bcf8827649f070c487800a95c35be3eb2e83b5447921675040cea38af1c\",\"header\":{\"parent_hash\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"state_root_hash\":\"fbd89036ca934a53b14ebc99abcf64351008ac073848c0b384771036121a25cc\",\"body_hash\":\"5187b7a8021bf4f2c004ea3a54cfece1754f11c7624d2363c7f4cf4fddd1441e\",\"random_bit\":false,\"accumulated_seed\":\"d8908c165dee785924e7421a0fd0418a19d5daeec395fd505a92a0fd3117e428\",\"era_end\":{\"era_report\":{\"equivocators\":[],\"rewards\":[],\"inactive_validators\":[]},\"next_era_validator_weights\":[{\"validator\":\"010d23984fefcce099679a24496f1d3072a540b95d321f8ba951df0cfe2c0691e5\",\"weight\":\"2000000000000004\"},{\"validator\":\"011213e00a3bd748278b38a00a4787a7143f28a9d564126566716a53daa9499852\",\"weight\":\"2000000000000006\"},{\"validator\":\"0180b99ded1d271c61a26d1b18c289ab33fc64355fa90cda4ae18f91786aa6ba4b\",\"weight\":\"2000000000000010\"},{\"validator\":\"01959d01aa68197e8cb91aa06bcc920f8d4a245dff60ea726bb89255349107a565\",\"weight\":\"2000000000000002\"},{\"validator\":\"01fcf1392c59c7d89190bfcd1b00902cc0801700eab98034aa4e56816d338f6c25\",\"weight\":\"2000000000000008\"}]},\"timestamp\":\"2022-07-22T16:56:37.891Z\",\"era_id\":0,\"height\":0,\"protocol_version\":\"1.0.0\"},\"body\":{\"proposer\":\"00\",\"deploy_hashes\":[],\"transfer_hashes\":[]},\"proofs\":[]}}}";
        final String idTxt = "id:2\n";

        final Stream<Event<String>> stream = EventService.usingPeer(source).readEvent(EventType.MAIN, EventTarget.RAW, new StringReader(dataTxt + "\n" + idTxt));
        final Optional<Event<String>> first = stream.findFirst();

        assertThat(first.isPresent(), is(true));

        //noinspection resource
        final String kafkaEvent = new String(new CsprEventSerializer().serialize("main", first.get()), StandardCharsets.UTF_8);
        assertThat(kafkaEvent, is(notNullValue()));

        assertThat(kafkaEvent, hasJsonPath("$.id", is(2)));
        assertThat(kafkaEvent, hasJsonPath("$.source", is(source)));
        assertThat(kafkaEvent, hasJsonPath("$.type", is("main")));
        assertThat(kafkaEvent, hasJsonPath("$.dataType", is("BlockAdded")));
        assertThat(kafkaEvent, hasJsonPath("$.data"));
        assertThat(kafkaEvent, hasJsonPath("$.data.BlockAdded"));
        assertThat(kafkaEvent, hasJsonPath("$.data.BlockAdded.block_hash", is("bb878bcf8827649f070c487800a95c35be3eb2e83b5447921675040cea38af1c")));
    }
}