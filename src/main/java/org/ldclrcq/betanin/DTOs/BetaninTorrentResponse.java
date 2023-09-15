package org.ldclrcq.betanin.DTOs;

import java.util.List;

public record BetaninTorrentResponse(int total, List<BetaninTorrentDTO> torrents) {
}
