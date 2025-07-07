package Musicfy.MusicfyOrigin.Product.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
    private  Long Id;
    @NotBlank(message = "Firebase UID é obrigatório")
    private String firebaseUid;

    @NotBlank(message = "Nome completo é obrigatório")
    private String fullName;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    public void setId(Long id) {
    }
}
