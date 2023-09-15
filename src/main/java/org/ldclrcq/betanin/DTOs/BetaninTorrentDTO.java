package org.ldclrcq.betanin.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record BetaninTorrentDTO(String id, String path, String status, String name, @JsonProperty("has_lines") Boolean hasLines, String tooltip, String updated, String created) {
}
