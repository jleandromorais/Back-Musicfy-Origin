package Musicfy.MusicfyOrigin.Product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class EnderecoDTO {
    private Long id;
    private  String cep;
    private String rua;
    private  String numero;
    private  String complemento;
    private  String bairro;
    private  String Cidade;
    private  String estado;
    private  String tipo;

    public EnderecoDTO(Long id, String cep, String bairro, String cidade, String estado, String complemento, String rua, String tipo) {
    }
}
