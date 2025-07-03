package Musicfy.MusicfyOrigin.Product.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table (name ="endereco " ,schema = "musicfy")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)// Coluna obrigatória no banco de dados.
    private  String cep;

    @Column (nullable = false )
    private String rua;

    @Column (nullable = false)
    private  String numero;

    private  String complemento;

    @Column (nullable = false)
    private  String bairro;

    @Column (nullable = false)
    private  String Cidade;

    @Column (nullable = false)
    private  String estado;

    @Column (nullable = false)
    private  String tipo;






}
