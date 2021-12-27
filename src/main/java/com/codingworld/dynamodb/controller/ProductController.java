package com.codingworld.dynamodb.controller;

import com.codingworld.dynamodb.bean.Product;
import com.codingworld.dynamodb.service.ProductService;
import java.util.List;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController
{

  @Autowired
  ProductService productService;

  @PostMapping(value = "saveProduct", consumes = "application/json")
  public ResponseEntity saveProduct(@RequestBody Product product)
  {
    try
    {
      productService.saveProduct(product);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(value = "/getproducts", produces = {"application/json"})
  public ResponseEntity<Product> getAllProduct(@PathParam("id") String id, @PathParam("name") String name)
  {

    try
    {
      return new ResponseEntity(productService.getProducts(id, name), HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping(value = "/deletetable/{tableName}")
  public ResponseEntity deletTable(@PathParam("tableName") String tableName)
  {
    try
    {
      productService.deleteTable(tableName);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping(value = "/updateproduct")
  public ResponseEntity updateProduct(
      @PathParam("name") String name,
      @PathParam("id") String id,
      @PathParam("quantity") Integer quantity
  )
  {
    try
    {
      productService.updateProduct(id, name, quantity);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping(value = "/deleteproduct")
  public ResponseEntity deleteProduct(@PathParam("name") String name,
      @PathParam("id") String id)
  {
    try
    {
      productService.deleteProduct(id, name);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @GetMapping(value = "/getproducts")
  public ResponseEntity<List<Product>> getProducts()
  {
    try
    {
      return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }
    catch (Exception e)
    {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}

