package claudiogiasi.progettosettimana3.security;

import claudiogiasi.progettosettimana3.entities.User;
import claudiogiasi.progettosettimana3.exceptions.NotFoundException;
import claudiogiasi.progettosettimana3.exceptions.UnauthorizedException;
import claudiogiasi.progettosettimana3.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class JWTFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtTool jwtTool;
    private final UserService userService;

    public JWTFilter(JwtTool jwtTool, UserService userService) {
        this.jwtTool = jwtTool;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Inserire il token nell'header");
            return;
        }

        String accessToken = header.replace("Bearer ", "");
        User currentUser;
        try {
            jwtTool.verifyToken(accessToken);
            UUID currentUserId = jwtTool.extractIdFromToken(accessToken);
            currentUser = userService.findById(currentUserId);
        } catch (UnauthorizedException | NotFoundException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token non valido, rifare il login");
            return;
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PATH_MATCHER.match("/api/auth/**", request.getServletPath());
    }
}
