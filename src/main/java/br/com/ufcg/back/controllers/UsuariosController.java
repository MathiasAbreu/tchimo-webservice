package br.com.ufcg.back.controllers;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Controle de Usuários da API")
@RestController
@RequestMapping("usuarios")
public class UsuariosController {
}
