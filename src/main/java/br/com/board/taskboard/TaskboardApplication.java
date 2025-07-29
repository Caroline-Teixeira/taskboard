package br.com.board.taskboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.board.taskboard.view.Menu;

@SpringBootApplication
public class TaskboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskboardApplication.class, args)
		.getBean(Menu.class)
            .start();
	}
// fix menu e build
}
