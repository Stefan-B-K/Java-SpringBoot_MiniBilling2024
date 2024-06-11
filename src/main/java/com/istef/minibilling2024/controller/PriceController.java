package com.istef.minibilling2024.controller;

import com.istef.minibilling2024.dao.db.PriceDbDAO;
import com.istef.minibilling2024.entity.Price;
import com.istef.minibilling2024.exception.ErrorDetails;
import com.istef.minibilling2024.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("prices")
public class PriceController {
    private final PriceDbDAO priceDbDAO;


    public PriceController(PriceDbDAO priceDbDAO) {
        this.priceDbDAO = priceDbDAO;
    }


    @PostMapping
    public ResponseEntity<Object> addPrice(@Valid @RequestBody Price price,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return ErrorDetails.fieldNotValidResponse(bindingResult);

        return DataService.responseForPostSave(String.valueOf(
                        priceDbDAO.save(price).getId()));
    }

}
