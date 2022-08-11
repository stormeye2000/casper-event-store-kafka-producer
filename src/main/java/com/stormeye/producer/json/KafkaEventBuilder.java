package com.stormeye.producer.json;


import com.casper.sdk.model.event.Event;

/**
 * Converts an SDK Event to a JSON String representation of that event for sending to Kafka
 *
 * @author ian@meywood.com
 */
public class KafkaEventBuilder {

    public static String buildKafkaEvent(final Event<String> rawEvent) {

        //noinspection StringBufferReplaceableByString
        return new StringBuilder("{\n  \"source\":\"")
                .append(rawEvent.getSource()).append("\",\n")
                .append("  \"type\":\"")
                .append(rawEvent.getEventType().name().toLowerCase())
                .append("\",\n")
                .append("  \"dataType\":\"")
                .append(rawEvent.getDataType().getDataTypeName())
                .append("\",\n")
                .append(appendId(rawEvent))
                .append(correctJson(rawEvent.getData()))
                .append("\n}")
                .toString();
    }

    /**
     * The CSPR data event is not valid JSON as the data: is not quoted as a key this method resolved that
     *
     * @param data the data to correct
     * @return the corrected data with the data key quoted
     */
    private static String correctJson(final String data) {
        final int start = data.indexOf(':');
        return "\"data\"" +  data.substring(start);
    }

    private static String appendId(final Event<String> rawEvent) {

        final StringBuilder builder = new StringBuilder("");

        rawEvent.getId().ifPresent(id -> {
            builder.append("  \"id\":");
            builder.append(id);
            builder.append(",\n");
        });

        return builder.toString();
    }

}
