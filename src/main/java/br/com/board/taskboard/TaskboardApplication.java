package br.com.board.taskboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.board.taskboard.view.MainMenu;

@SpringBootApplication
public class TaskboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskboardApplication.class, args)
		.getBean(MainMenu.class)
            .start();
	}

}
