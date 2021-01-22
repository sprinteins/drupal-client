package com.sprinteins.drupalcli;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/entity/paragraph")
public class DrupalMockApplication {
    @PatchMapping("/{id}")
    public void patch(@PathVariable() long id) {}
}
