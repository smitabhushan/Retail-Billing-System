package in.ankitdaksh.billingsoftware.service;

import in.ankitdaksh.billingsoftware.io.UserRequest;
import in.ankitdaksh.billingsoftware.io.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    String getUserRole(String email);
    List<UserResponse> readUsers();
    void deleteUser(String id);
}
