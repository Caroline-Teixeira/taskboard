package br.com.board.taskboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.board.taskboard.model.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {


}
