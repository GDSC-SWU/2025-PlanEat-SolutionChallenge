package com.gdgswu.planeat.domain.auth.dto.request;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String idToken;
    private String name;
    private String gender;
    private int age;
    private int height;
    private int weight;
    private String location;
    private String dietaryKeyword;
}
