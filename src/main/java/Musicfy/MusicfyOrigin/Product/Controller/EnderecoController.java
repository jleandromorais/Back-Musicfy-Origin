package Musicfy.MusicfyOrigin.Product.Controller;

import Musicfy.MusicfyOrigin.Product.Service.EnderecoService;
import Musicfy.MusicfyOrigin.Product.dto.EnderecoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enderecos")
@CrossOrigin(origins = "http://localhost:5173")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @PostMapping
    public ResponseEntity<EnderecoDTO> criarEndereco(@RequestBody EnderecoDTO enderecoDTO) {
        EnderecoDTO criado = enderecoService.CriarEndereco(enderecoDTO);
        return ResponseEntity.status(201).body(criado); // HTTP 201 Created
    }
}
