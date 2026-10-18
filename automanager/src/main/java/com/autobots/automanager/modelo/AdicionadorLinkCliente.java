package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Telefone;

@Component
public class AdicionadorLinkCliente implements AdicionadorLink<Cliente> {
	@Autowired
	private AdicionadorLinkDocumento adicionadorLinkDocumento;
	@Autowired
	private AdicionadorLinkTelefone adicionadorLinkTelefone;
	@Autowired
	private AdicionadorLinkEndereco adicionadorLinkEndereco;

	// Em listas: cada cliente ganha o link para si mesmo (self)
	@Override
	public void adicionarLink(List<Cliente> lista) {
		for (Cliente cliente : lista) {
			adicionarLinkProprio(cliente);
			adicionarLinksInternos(cliente);
		}
	}

	@Override
	public void adicionarLink(Cliente objeto) {
		adicionarLinkProprio(objeto);
		Link linkColecao = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(ClienteControle.class)
						.obterClientes())
				.withRel("clientes");
		objeto.add(linkColecao);
		adicionarLinksInternos(objeto);
	}

	private void adicionarLinkProprio(Cliente cliente) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(ClienteControle.class)
						.obterCliente(cliente.getId()))
				.withSelfRel();
		cliente.add(linkProprio);
	}

	private void adicionarLinksInternos(Cliente cliente) {
		for (Documento documento : cliente.getDocumentos()) {
			adicionadorLinkDocumento.adicionarLinkProprio(documento);
		}
		for (Telefone telefone : cliente.getTelefones()) {
			adicionadorLinkTelefone.adicionarLinkProprio(telefone);
		}
		if (cliente.getEndereco() != null) {
			adicionadorLinkEndereco.adicionarLinkProprio(cliente.getEndereco());
		}
	}
}