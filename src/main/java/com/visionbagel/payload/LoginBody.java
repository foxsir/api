package com.visionbagel.payload;


import jakarta.validation.constraints.*;

public class LoginBody {
    @Size(min = 2, max = 32)
    @NotBlank()
    @NotNull()
    @NotEmpty()
    public String account;

    @Size(min = 6, max = 64)
    @NotBlank()
    @NotNull()
    @NotEmpty()
    public String password;
}
