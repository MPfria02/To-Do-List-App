package todo.app.mapper;

import todo.app.logic.User;
import todo.app.logic.UserDTO;

public class UserMapper {
	
    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getEntityId(), user.getUsername(), user.getEmail());
    }

    public static User toEntity(UserDTO userDTO) {
        return new User(userDTO.getUsername(), userDTO.getEmail(), null); // Password not included in DTO
    }
}
