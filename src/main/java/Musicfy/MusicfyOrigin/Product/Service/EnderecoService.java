package Musicfy.MusicfyOrigin.Product.Service;

import jakarta.transaction.Transactional;
import Musicfy.MusicfyOrigin.Product.dto.EnderecoDTO;
import Musicfy.MusicfyOrigin.Product.model.Endereco;
import Musicfy.MusicfyOrigin.Product.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {


    @Autowired
    EnderecoRepository enderecoRepository;

    @Transactional
    public EnderecoDTO CriarEndereco(EnderecoDTO enderecoDTO) {
        // 1. Criar nova entidade

        Endereco novoEndereco = new Endereco();
// 2. Preencher os campos da entidade com os dados do DTO
        novoEndereco.setCep(enderecoDTO.getCep());
        novoEndereco.setRua(enderecoDTO.getRua());
        novoEndereco.setNumero(enderecoDTO.getNumero());
        novoEndereco.setComplemento(enderecoDTO.getComplemento());
        novoEndereco.setBairro(enderecoDTO.getBairro());
        novoEndereco.setCidade(enderecoDTO.getCidade());
        novoEndereco.setEstado(enderecoDTO.getEstado());
        novoEndereco.setTipo(enderecoDTO.getTipo());


        // 3. Salvar no banco
        Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);

        // 4. Criar DTO com base no objeto salvo
        EnderecoDTO enderecoRetorno = new EnderecoDTO();
        enderecoRetorno.setId(enderecoSalvo.getId());
        enderecoRetorno.setCep(enderecoSalvo.getCep());
        enderecoRetorno.setRua(enderecoSalvo.getRua());
        enderecoRetorno.setNumero(enderecoSalvo.getNumero());
        enderecoRetorno.setComplemento(enderecoSalvo.getComplemento());
        enderecoRetorno.setBairro(enderecoSalvo.getBairro());
        enderecoRetorno.setCidade(enderecoSalvo.getCidade());
        enderecoRetorno.setEstado(enderecoSalvo.getEstado());
        enderecoRetorno.setTipo(enderecoSalvo.getTipo());

        // 5. Retornar o DTO preenchido
        return enderecoRetorno;

    }
}
