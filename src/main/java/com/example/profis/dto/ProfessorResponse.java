package com.example.profis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorResponse {
    private Long id;
    private String salutation;
    private String firstName;
    private String lastName;
    private String fullName;
    private Boolean isExternal;
    private String phone;
    private String email;
    private String address;
    private String comment;
}
