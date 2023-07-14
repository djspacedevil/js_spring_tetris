package de.hsel.tetris.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class WebController {

    @Operation(summary = "Display logged in users principal name", security = @SecurityRequirement(name = "User Token"))
    @GetMapping
    @ResponseBody
    public String index(Principal principal) {
        return "Web Client Entry Point. Principal name: " + principal.getName();
    }
}
