package it.unibo.controller.server.persistence.backup;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;

import java.util.Optional;

public interface GameSnapshotRepository {
    Optional<MatchSnapshot> findLatestSnapshot(String matchId);
}
