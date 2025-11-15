package com.example.variant4;

import com.example.variant4.repo.UserRepository;
import com.example.variant4.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    @Test
    void existByEmail_shouldReturnFalse_whenUserDoesNotExist() {
        String email = "nope@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean exists = userService.existByEmail(email);

        assertThat(exists).isFalse();
        verify(userRepository).existsByEmail(email);
    }
}
