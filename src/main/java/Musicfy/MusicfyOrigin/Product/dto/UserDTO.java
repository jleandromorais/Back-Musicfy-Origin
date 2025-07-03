package Musicfy.MusicfyOrigin.Product.dto;

import lombok.Data;

@Data
public class UserDTO {
    private  Long id;
    private String firebaseUid;
    private String fullName;
    private String email;
}