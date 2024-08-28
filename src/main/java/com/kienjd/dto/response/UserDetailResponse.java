package com.kienjd.dto.response;

import lombok.Builder;
import lombok.Getter;
import com.kienjd.util.Gender;
import com.kienjd.util.UserStatus;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
public class UserDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private String type;

    private UserStatus status;
}
