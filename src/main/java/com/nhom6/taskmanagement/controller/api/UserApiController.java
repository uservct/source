package com.nhom6.taskmanagement.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nhom6.taskmanagement.dto.password.ChangePasswordDTO;
import com.nhom6.taskmanagement.dto.user.UserResponseDTO;
import com.nhom6.taskmanagement.dto.user.UserUpdateDTO;
import com.nhom6.taskmanagement.exception.InvalidPasswordException;
import com.nhom6.taskmanagement.exception.ResourceNotFoundException;
import com.nhom6.taskmanagement.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    @Autowired
    private UserService userService;

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDTO(id));
    }

    // Update user
    //  Authorize for ADMIN or current user
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDTO));
    }

    // Reset password
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<String> resetPassword(@PathVariable Long id) {
        return ResponseEntity.ok(userService.resetPassword(id));
    }

    // Delete user
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @PostMapping("/profile/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO request) {
        try {
            // Gọi service để thay đổi mật khẩu
            String message = userService.changePassword(request);
            return ResponseEntity.ok(message); // Trả về thông báo thành công
        } catch (InvalidPasswordException ex) {
            // Nếu mật khẩu hiện tại không đúng, hoặc các lỗi liên quan đến mật khẩu
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            // Nếu không tìm thấy người dùng
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            // Xử lý các lỗi không xác định
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi không xác định");
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
    @PostMapping("/{id}/avatar")
    public ResponseEntity<UserResponseDTO> updateAvatar(
            @PathVariable Long id,
            @RequestParam("avatar") MultipartFile file) {
        return ResponseEntity.ok(userService.updateAvatar(id, file));
    }
}
