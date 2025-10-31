package com.jidang.Game;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; //값을 없을때 NullPointerException을 피하기 위한 안전한 타입

public interface GameRepository extends JpaRepository<Game, Long>{
    Optional<Game> findBySlug(String slug);
    Optional<Game> findByName(String name);
}
