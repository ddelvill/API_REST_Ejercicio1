package com.example.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Cliente;
import com.example.services.ClienteService;



@RestController
@RequestMapping("/clientes")
public class ClienteController {

   @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> findAll(@RequestParam(name = "page", required = false) Integer page,
                                                @RequestParam(name = "size", required = false) Integer size) {

        ResponseEntity<List<Cliente>> responseEntity = null;
        List<Cliente> clientes = new ArrayList<>();
        Sort sortByNombre = Sort.by("nombre");


        if (page != null && size != null) {

            try {

                Pageable pageable = PageRequest.of(page, size, sortByNombre);
    
                Page<Cliente> clientesPaginados = clienteService.findAll(pageable);
    
                clientes = clientesPaginados.getContent();
    
                responseEntity = new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);
            
            } catch (Exception e) {
                responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
             }



            } else {
                try {
                    clientes = clienteService.findAll(sortByNombre);
                    responseEntity = new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);
                } catch (Exception e) {
                    responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }


            }
            











    return responseEntity;
    }    
}
