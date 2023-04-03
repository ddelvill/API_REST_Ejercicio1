package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Cliente;
import com.example.models.FileUploadResponse;
import com.example.services.ClienteService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private FileDownloadUtil fileDownloadUtil;

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
                responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        }

        return responseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable(name = "id") Integer id) {

        ResponseEntity<Map<String, Object>> responseEntity = null;

        Map<String, Object> responseAsMap = new HashMap<>();

        try {


            Cliente cliente = clienteService.findById(id);

            if (cliente != null) {
                String successMessage = "Se ha encontrado el cliente con id: " + id + " correctamente";
                responseAsMap.put("mensaje", successMessage);
                responseAsMap.put("cliente", cliente);
                // responseAsMap.put("mascotas", cliente.getMascotas());
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);

            } else {

                String errorMessage = "No se ha encontrado el cliente con id: " + id;
                responseAsMap.put("error", errorMessage);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);

            }

        } catch (Exception e) {

            String errorGrave = "Error grave";
            responseAsMap.put("error", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;
    }





    @PostMapping( consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<Map<String, Object>> insert(
                            @Valid 
                            @RequestPart(name = "cliente") Cliente cliente, 
                            BindingResult result,
                            @RequestPart (name = "file") MultipartFile file ) throws IOException {

        Map<String, Object> responseAsMap = new HashMap<>();

        ResponseEntity<Map<String, Object>> responseEntity = null;



         if (result.hasErrors()) {

            List<String> errorMessages = new ArrayList<>();

            for( ObjectError error : result.getAllErrors()) {

                errorMessages.add(error.getDefaultMessage());

            }

            // var prueba = result.getAllErrors();

            // prueba.stream().forEach(e -> {
            //     errorMessages.add(e.getDefaultMessage());
            // });


            responseAsMap.put("errores", errorMessages);

            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        




         }

         if(!file.isEmpty()) {
            String fileCode =  fileUploadUtil.saveFile(file.getOriginalFilename(), file);
            cliente.setImagenCliente(fileCode + "-" + file.getOriginalFilename());

            // Devolver informacion respecto al file recibido. 

            FileUploadResponse fileUploadResponse = FileUploadResponse
                .builder()
                .fileName(fileCode + "-" + file.getOriginalFilename())
                .downloadURI("/clientes/downloadFile/" + fileCode + "-" + file.getOriginalFilename())
                .size(file.getSize())
                .build();

            responseAsMap.put("info de la imagen: ", fileUploadResponse);


         }

         Cliente clienteDB = clienteService.save(cliente);

         try {

            if (clienteDB != null) {

                String mensaje = "El cliente se ha creado correctamente" ;
                responseAsMap.put("mensaje", mensaje);
                responseAsMap.put("cliente", clienteDB);
                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);
    
             } else {

                String errorMensaje = "El usuario no se ha creado correctamente" ;

    
                responseAsMap.put("mensaje", errorMensaje);

                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR );
    
             }
            
         } catch (DataAccessException e) {

            String errorGrave = "Ha tenido lugar un error grave  y, la causa más problable puede ser: " + e.getMostSpecificCause();

           responseAsMap.put("errorGrave", errorGrave);

           responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        
        }



        return responseEntity;
    }


   

}
