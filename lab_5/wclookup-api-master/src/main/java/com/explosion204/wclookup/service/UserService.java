package com.explosion204.wclookup.service;

import com.explosion204.wclookup.exception.EntityNotFoundException;
import com.explosion204.wclookup.model.entity.User;
import com.explosion204.wclookup.model.repository.UserRepository;
import com.explosion204.wclookup.service.dto.identifiable.UserDto;
import com.explosion204.wclookup.service.pagination.PageContext;
import com.explosion204.wclookup.service.pagination.PaginationModel;
import com.explosion204.wclookup.service.validation.annotation.ValidateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PaginationModel<UserDto> findAll(PageContext pageContext) {
        PageRequest pageRequest = pageContext.toPageRequest();
        Page<UserDto> page = userRepository.findAll(pageRequest)
                .map(UserDto::fromUser);
        return PaginationModel.fromPage(page);
    }

    public UserDto findById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
        return UserDto.fromUser(user);
    }

    public UserDto findCurrent() {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return UserDto.fromUser(user);
    }

    @ValidateDto
    public UserDto update(UserDto userDto) {
        if (userDto.getNickname() != null) {
            User user = userRepository.findById(userDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException(User.class));

            user.setNickname(user.getNickname());
            userRepository.save(user);
        }

        return userDto;
    }
}
