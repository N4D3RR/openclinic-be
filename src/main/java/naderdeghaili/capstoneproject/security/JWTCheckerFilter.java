package naderdeghaili.capstoneproject.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.exceptions.UnauthorizedException;
import naderdeghaili.capstoneproject.services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JWTCheckerFilter extends OncePerRequestFilter {

    private final JWTTools jwtTools;
    private final UserService userService;

    public JWTCheckerFilter(JWTTools jwtTools, UserService userService) {
        this.jwtTools = jwtTools;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // AUTENTICAZIONE
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new UnauthorizedException("Missing or invalid Authorization header");

        String accessToken = authHeader.substring(7);

        jwtTools.verifyToken(accessToken);

        // AUTORIZZAZIONE
        UUID userId = jwtTools.getIdFromToken(accessToken);
        User authorizedUser = userService.findByID(userId);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authorizedUser,
                null,
                authorizedUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher matcher = new AntPathMatcher(); //autorizzo i sotto percorsi
        String path = request.getServletPath();

        return matcher.match("/auth/**", path) //autorizzo auth/login, auth/register
                || matcher.match("/swagger-ui/**", path)
                || matcher.match("/swagger-ui.html", path)
                || matcher.match("/api-docs/**", path)
                || matcher.match("/api-docs", path);
    }
}
