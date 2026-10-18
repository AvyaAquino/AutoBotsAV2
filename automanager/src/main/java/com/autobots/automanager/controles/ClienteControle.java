package com.autobots.automanager.controles;

import java.util.Date;
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
import com.autobots.automanager.modelo.AdicionadorLinkCliente;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/cliente")
public class ClienteControle {
	@Autowired
	private ClienteRepositorio repositorio;
	@Autowired
	private AdicionadorLinkCliente adicionadorLink;

	@GetMapping("/{id}")
	public ResponseEntity<Cliente> obterCliente(@PathVariable long id) {
		Cliente cliente = repositorio.findById(id).orElse(null);
		if (cliente == null) {
			return ResponseEntity.notFound().build();
		}
		adicionadorLink.adicionarLink(cliente);
		return ResponseEntity.ok(cliente);
	}

	@GetMapping("/clientes")
	public ResponseEntity<List<Cliente>> obterClientes() {
		List<Cliente> clientes = repositorio.findAll();
		adicionadorLink.adicionarLink(clientes);
		return ResponseEntity.ok(clientes);
	}

	@PostMapping("/cadastro")
	public ResponseEntity<Cliente> cadastrarCliente(@RequestBody Cliente cliente) {
		if (cliente.getId() != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		if (cliente.getDataCadastro() == null) {
			cliente.setDataCadastro(new Date());
		}
		if ("Tae Takemi".equalsIgnoreCase(cliente.getNome())) {
			System.out.println("Melhor Confidente de P5 - Ávya");
		}
		Cliente salvo = repositorio.save(cliente);
		adicionadorLink.adicionarLink(salvo);
		return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	}

	@PutMapping("/atualizar")
	public ResponseEntity<Cliente> atualizarCliente(@RequestBody Cliente atualizacao) {
		if (atualizacao.getId() == null) {
			return ResponseEntity.badRequest().build();
		}
		Cliente cliente = repositorio.findById(atualizacao.getId()).orElse(null);
		if (cliente == null) {
			return ResponseEntity.notFound().build();
		}
		ClienteAtualizador atualizador = new ClienteAtualizador();
		atualizador.atualizar(cliente, atualizacao);
		repositorio.save(cliente);
		adicionadorLink.adicionarLink(cliente);
		return ResponseEntity.ok(cliente);
	}

	@DeleteMapping("/excluir")
	public ResponseEntity<Void> excluirCliente(@RequestBody Cliente exclusao) {
		if (exclusao.getId() == null) {
			return ResponseEntity.badRequest().build();
		}
		Cliente cliente = repositorio.findById(exclusao.getId()).orElse(null);
		if (cliente == null) {
			return ResponseEntity.notFound().build();
		}
		repositorio.delete(cliente);
		return ResponseEntity.ok().build();
	}
}