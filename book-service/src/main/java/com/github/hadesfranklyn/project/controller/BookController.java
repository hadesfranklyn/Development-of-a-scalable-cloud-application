package com.github.hadesfranklyn.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.hadesfranklyn.project.model.Book;
import com.github.hadesfranklyn.project.proxy.CambioProxy;
import com.github.hadesfranklyn.project.repository.BookRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Book endpoint")
@RestController
@RequestMapping("book-service")
public class BookController {

	@Autowired
	private BookRepository repository;

	@Autowired
	private Environment environment;

	@Autowired
	private CambioProxy proxy;

	@Operation(summary = "Find a specific book by your ID")
	@GetMapping(value = "/{id}/{currency}")
	public Book findBook(@PathVariable("id") Long id, @PathVariable("currency") String currency) {

		@SuppressWarnings("deprecation")
		var book = repository.getById(id);

		if (book == null)
			throw new RuntimeException("Book not Found");

		var cambio = proxy.getCambio(book.getPrice(), "USD", currency);

		var port = environment.getProperty("local.server.port");
		book.setEnviroment(
				"Book port: "+ port + 
				" Cambio port: " + cambio.getEnvironment());
		book.setPrice(cambio.getConvertedValue());
		return book;
	}
}
