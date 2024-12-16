package com.nhom6.taskmanagement.service;

public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    // Get user by id
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Get users by ids
    public List<User> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    // Get users by project id
    public List<User> getUsersByProjectId(Long projectId) {
        return userRepository.findByProjects_Id(projectId);
    }

    // Get user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Get current user
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No authenticated user found");
        }
        
        return userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user dto
    public UserResponseDTO getUserDTO(Long id) {
        return userMapper.toResponseDTO(getUserById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    // Get all users dto
    public List<UserResponseDTO> getAllUsersDTO() {
        return userMapper.toResponseDTOs(getAllUsers());
    }
}
