package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.model.Rol;
import net.andrecarbajal.sysped.model.Staff;
import net.andrecarbajal.sysped.repository.StaffRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class StaffDetailsServiceImpl implements UserDetailsService {
    private final StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Staff appStaff = staffRepository.findByDniAndActiveTrue(dni)
                .orElseThrow(() -> new UsernameNotFoundException("Staff not found or inactive: " + dni));

        return new User(appStaff.getDni(),
                appStaff.getPassword(),
                mapRoleToAuthority(appStaff.getRol()));
    }

    private Collection<? extends GrantedAuthority> mapRoleToAuthority(Rol rol) {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + rol.getName().toUpperCase())
        );
    }
}
