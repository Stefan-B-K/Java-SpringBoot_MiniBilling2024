package com.istef.minibilling2024.controller;

import com.istef.minibilling2024.entity.Reading;
import com.istef.minibilling2024.exception.ErrorDetails;
import com.istef.minibilling2024.service.DataService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("users")
public class ReadingController {
    private final DataService dataService;

    public ReadingController(DataService dataService) {
        this.dataService = dataService;
    }


    @PostMapping("/{ref}/readings")
    public ResponseEntity<Object> addReadingForUser(@Parameter(description = "User ID")
                                                    @Schema(type = "string", example = "4")
                                                    @PathVariable int ref,
                                                    @RequestBody Reading reading,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return ErrorDetails.fieldNotValidResponse(bindingResult);

        String userRef = String.valueOf(ref);

        return DataService.responseForPostSave(String.valueOf(
                dataService.addReading(reading, userRef)));
    }


    @GetMapping("/{ref}/readings")
    public List<Reading> getReadingsForUser(@Parameter(description = "User ID")
                                            @Schema(type = "string", example = "4")
                                            @PathVariable int ref) {

        return dataService.listReadingsForUser(String.valueOf(ref));
    }
}
