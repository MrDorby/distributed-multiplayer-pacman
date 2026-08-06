package it.unibo.mongodb;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object representing the "checkpoint" stored.
 * @param context the context.
 * @param timestamp the time of the checkpoint.
 */
public record Checkpoint(
    @JsonProperty("context") Object context,
    @JsonProperty("timeLeft") Long timestamp) {
        
}