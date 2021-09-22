package com.sprinteins.drupalcli.mock;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@RequestMapping("/entity/paragraph")
public class DrupalMockApplication {
    @PatchMapping("/{id}")
    public String patch(@PathVariable() long id, @RequestBody String requestBody) {
        return requestBody;
    }
}
