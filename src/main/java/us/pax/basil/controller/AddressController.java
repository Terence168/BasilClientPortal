package us.pax.basil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.pax.basil.entity.customer.Address;
import us.pax.basil.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("/customer/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @GetMapping
    public List<Address> findAll() {
        return addressService.findAll();
    }
    
    @GetMapping("/{xaOid}")
    public Address findById(@PathVariable int xaOid) {
        return addressService.findById(xaOid);
    }
    
    @PostMapping
    public int insert(@RequestBody Address address) {
        return addressService.insert(address);
    }
    
    @PutMapping
    public int update(@RequestBody Address address) {
        return addressService.update(address);
    }
    
    @DeleteMapping("/{xaOid}")
    public int delete(@PathVariable int xaOid) {
        return addressService.delete(xaOid);
    }
}
