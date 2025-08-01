package br.com.board.taskboard.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.board.taskboard.model.Card;
import br.com.board.taskboard.model.TaskStatus;
import br.com.board.taskboard.repository.CardRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class CardServiceIntegrationTest {
    @Autowired
    private CardService cardService;
    @Autowired
    private CardRepository cardRepository;

    @Test
    void deleteCard_ValidCardId_DeletesCard() {
        Card card = new Card();
        card.setTitle("Teste");
        card.setCreatedAt(LocalDateTime.now());
        card.setTaskStatus(new TaskStatus());
        card.setMovements(new ArrayList<>());
        card.setBlockHistories(new ArrayList<>());
        card = cardRepository.save(card);

        cardService.deleteCard(card.getId());

        assertTrue(cardRepository.findById(card.getId()).isEmpty());
    }
}