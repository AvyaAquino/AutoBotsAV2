package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.modelo.AdicionadorLinkEndereco;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {
	@Autowired
	private EnderecoRepositorio repositorio;
	@Autowired
	private AdicionadorLinkEndereco adicionadorLink;
	@Autowired
	private ClienteRepositorio clienteRepositorio;

	@GetMapping("/{id}")
	public ResponseEntity<Endereco> obterEndereco(@PathVariable long id) {
		Endereco endereco = repositorio.findById(id).orElse(null);
		if (endereco == null) {
			return ResponseEntity.notFound().build();
		}
		adicionadorLink.adicionarLink(endereco);
		return ResponseEntity.ok(endereco);
	}

	@GetMapping("/enderecos")
	public ResponseEntity<List<Endereco>> obterEnderecos() {
		List<Endereco> enderecos = repositorio.findAll();
		adicionadorLink.adicionarLink(enderecos);
		return ResponseEntity.ok(enderecos);
	}

	@GetMapping("/cliente/{clienteId}")
	public ResponseEntity<Endereco> obterEnderecoDoCliente(@PathVariable long clienteId) {
		Cliente cliente = clienteRepositorio.findById(clienteId).orElse(null);
		if (cliente == null || cliente.getEndereco() == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(cliente.getEndereco());
	}

	@PostMapping("/cadastro/{clienteId}")
	public ResponseEntity<Cliente> cadastrarEndereco(@PathVariable long clienteId,
			@RequestBody Endereco endereco) {
		Cliente cliente = clienteRepositorio.findById(clienteId).orElse(null);
		if (cliente == null) {
			return ResponseEntity.notFound().build();
		}
		if (cliente.getEndereco() != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		endereco.setId(null);
		cliente.setEndereco(endereco);
		return ResponseEntity.status(HttpStatus.CREATED).body(clienteRepositorio.save(cliente));
	}

	@PutMapping("/atualizar")
	public ResponseEntity<Endereco> atualizarEndereco(@RequestBody Endereco atualizacao) {
		if (atualizacao.getId() == null) {
			return ResponseEntity.badRequest().build();
		}
		Endereco endereco = repositorio.findById(atualizacao.getId()).orElse(null);
		if (endereco == null) {
			return ResponseEntity.notFound().build();
		}
		new EnderecoAtualizador().atualizar(endereco, atualizacao);
		repositorio.save(endereco);
		adicionadorLink.adicionarLink(endereco);
		return ResponseEntity.ok(endereco);
	}

	@DeleteMapping("/excluir")
	public ResponseEntity<Void> excluirEndereco(@RequestBody Endereco exclusao) {
		if (exclusao.getId() == null) {
			return ResponseEntity.badRequest().build();
		}
		Cliente cliente = clienteRepositorio.findByEnderecoId(exclusao.getId());
		if (cliente == null) {
			return ResponseEntity.notFound().build();
		}
		cliente.setEndereco(null);
		clienteRepositorio.save(cliente);
		return ResponseEntity.ok().build();
	}
}
