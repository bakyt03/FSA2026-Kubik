package sk.posam.fsa.statstracker.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.posam.fsa.statstracker.domain.Player;

import java.util.Optional;

/**
 * Spring Data JPA repozitár pre entitu Player.
 * Poznámka: Player musí byť označený @Entity (alebo namapovaný cez ORM XML).
 */
public interface PlayerSpringDataRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByNickname(String nickname);
}
