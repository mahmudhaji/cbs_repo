package Core.Banking.System.CBS.controller;

import Core.Banking.System.CBS.DTOs.CustomerDTO;
import Core.Banking.System.CBS.model.Customer;
import Core.Banking.System.CBS.services.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO dto) {
        Customer customer = modelMapper.map(dto, Customer.class);
        Customer saved = customerService.createCustomer(customer);
        return ResponseEntity.ok(modelMapper.map(saved, CustomerDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        Customer customer = customerService.getCustomer(id);
        return ResponseEntity.ok(modelMapper.map(customer, CustomerDTO.class));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CustomerDTO>> bulkUpload(@RequestParam("file") MultipartFile file) {
        List<Customer> savedCustomers = customerService.bulkUpload(file);
        List<CustomerDTO> dtos = savedCustomers.stream()
                .map(c -> modelMapper.map(c, CustomerDTO.class))
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
