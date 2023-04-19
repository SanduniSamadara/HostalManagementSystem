package lk.ijse.hibernate.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class UserDTO {
    private String userId;
    private String name;
    private String telNo;
    private String email;
    private String userName;
    private String password;
}
