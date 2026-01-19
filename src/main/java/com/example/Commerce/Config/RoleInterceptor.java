package com.example.Commerce.Config;

import com.example.Commerce.Enums.UserRole;
import com.example.Commerce.errorHandlers.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class RoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler) throws Exception{
        if(!(handler instanceof HandlerMethod handlerMethod)) return true;
        RequiresRole annotation = handlerMethod.getMethodAnnotation(RequiresRole.class);

        if(annotation == null) return  true;

        String userRole = (String) request.getAttribute("authenticatedUserRole");

        for(UserRole requiredRole : annotation.value()){
            if(requiredRole.name().equals(userRole)){
                return true;
            }
        }
        throw  new UnauthorizedException("User does not have required role to access this resource");
    }
}
