package com.visionbagel.payload;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SendCodeBody {
    @Size(min = 11, max = 11)
    @NotBlank()
    @NotNull()
    @NotEmpty()
    public String mobile;
}
