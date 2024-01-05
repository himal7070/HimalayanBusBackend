package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.service.IUserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender javaMailSender;

    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getUserInformationByEmail(String email) throws UserException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            Map<String, Object> userInformation = new HashMap<>();
            userInformation.put("email", user.getEmail());
            if (user.getImageProfileUrl() != null) {
                userInformation.put("imageProfileUrl", user.getImageProfileUrl());
            }

            if (user.getPassenger() != null) {
                Passenger passenger = user.getPassenger();
                userInformation.put("firstName", passenger.getFirstName());
                userInformation.put("lastName", passenger.getLastName());
                userInformation.put("phoneNumber", passenger.getPhoneNumber());

            }

            return userInformation;
        } else {
            throw new UserException("User not found with this email!");
        }

    }



    @Override
    @Transactional(rollbackFor = UserException.class)
    public User updatePasswordByEmail(String email, String oldPassword, String newPassword) throws UserException {

        User existingUser = userRepository.findByEmail(email);

        if (existingUser != null) {
            if (newPassword != null && !newPassword.isEmpty()) {
                if (passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
                    String hashedNewPassword = passwordEncoder.encode(newPassword);
                    existingUser.setPassword(hashedNewPassword);
                    return userRepository.save(existingUser);
                } else {
                    throw new UserException("Old password is incorrect.");
                }
            } else {
                throw new UserException("New password cannot be empty.");
            }
        } else {
            throw new UserException("User not found for the provided email.");
        }

    }


    @Override
    @Transactional(rollbackFor = UserException.class)
    public void initiatePasswordReset(String userEmail) throws UserException {
        User user = userRepository.findByEmail(userEmail);
        if (user != null) {
            String resetToken = generateUniqueToken();
            LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);

            user.setResetToken(resetToken);
            user.setResetTokenExpiry(expiryTime);

            userRepository.save(user);
            sendResetEmail(user.getEmail(), resetToken);

        } else {
            throw new UserException("User not found for the provided email during password reset.");
        }
    }

    private void sendResetEmail(String userEmail, String resetToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("Your password reset token is: " + resetToken);

        javaMailSender.send(mailMessage);
    }


    private String generateUniqueToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }



    @Override
    @Transactional(rollbackFor = UserException.class)
    public void completePasswordReset(String email, String resetToken, String newPassword) throws UserException {
        User user = userRepository.findByEmail(email);
        if (user != null && resetToken.equals(user.getResetToken()) && LocalDateTime.now().isBefore(user.getResetTokenExpiry())) {
            String hashedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedNewPassword);
            //reset token and expiry
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
        } else {
            throw new UserException("Invalid or expired reset token.");
        }
    }





}
