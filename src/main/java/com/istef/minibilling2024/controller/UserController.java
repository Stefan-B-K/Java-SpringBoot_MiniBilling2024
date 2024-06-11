package com.istef.minibilling2024.controller;

import com.istef.minibilling2024.entity.User;
import com.istef.minibilling2024.exception.ErrorDetails;
import com.istef.minibilling2024.service.DataService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {
    private final DataService dataService;

    public UserController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public List<User> listAll() {
        return dataService.listAllUsers();
    }


    @GetMapping("/{ref}")
    public User getOne(@Parameter(description = "User ID")
                       @Schema(type = "string", example = "4")
                       @PathVariable int ref) {

        return dataService.getOneUser(String.valueOf(ref));
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return ErrorDetails.fieldNotValidResponse(bindingResult);

        return DataService.responseForPostSave(dataService.addUser(user));
    }

}
