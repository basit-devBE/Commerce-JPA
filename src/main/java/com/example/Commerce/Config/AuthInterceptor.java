package com.example.Commerce.Config;


import com.example.Commerce.Entities.UserEntity;
import com.example.Commerce.Repositories.UserRepository;
import com.example.Commerce.errorHandlers.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    public AuthInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, Object handler) throws Exception{
        String authHeader = request.getHeader("Authorization");
        log.info("Auth Header: {}", authHeader);
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);

        try{
            String[] parts = token.split("-");
            if(parts.length != 2){
                throw new UnauthorizedException("Invalid token format");
            }
            String userId = parts[1];
            UserEntity user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UnauthorizedException("Invalid token - user not found"));
            request.setAttribute("authenticatedUserId", user.getId());
            request.setAttribute("authenticatedUserRole", user.getRole().name());
            return true;
        }catch (NumberFormatException e){
            throw new UnauthorizedException("Invalid token format");
        }catch (UnauthorizedException e){
            throw e;
        }catch (Exception e){
            throw new UnauthorizedException("Invalid token");
        }
    }
}
