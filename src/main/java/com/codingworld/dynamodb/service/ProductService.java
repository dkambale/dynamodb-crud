package com.codingworld.dynamodb.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.codingworld.dynamodb.bean.Product;
import com.codingworld.dynamodb.constant.AppConstant;
import com.codingworld.dynamodb.repo.DynamoDBRepo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService
{

  @Autowired
  DynamoDBRepo dynamoDBRepo;

  public void createTable(String tableName) throws Exception
  {
    if (dynamoDBRepo.getTable(tableName) != null)
    {
      dynamoDBRepo.createProductTable();
    }
  }

  public void deleteTable(String tableName) throws Exception
  {
    dynamoDBRepo.deleteTable(tableName);
  }

  public void saveProduct(Product product) throws Exception
  {
    createTable(AppConstant.PRODUCT);
    Table table = dynamoDBRepo.getTable(AppConstant.PRODUCT);
    try
    {
      String id = UUID.randomUUID().toString();
      System.out.println("Id:" + id);
      PutItemOutcome outcome = table.putItem(new Item().withPrimaryKey("id", id).
          with("name", product.getName())
          .with("price", product.getPrice())
          .with("quantity", product.getQuantity()));
      System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
    }
    catch (Exception e)
    {
      System.out.println(e);
    }
  }

  public Product getProducts(String uuid, String name)
  {
    Product product = null;
    Table table = dynamoDBRepo.getTable(AppConstant.PRODUCT);
    if (table != null)
    {
      GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", uuid, "name", name);

      try
      {
        System.out.println("Attempting to read the item...");
        Item outcome = table.getItem(spec);
        if (outcome != null)
        {
          product = new Product();
          product.setId(outcome.getString("id"));
          product.setName(outcome.getString("name"));
          product.setPrice(outcome.getLong("price"));
          product.setQuantity(outcome.getInt("quantity"));
        }
        System.out.println("GetItem succeeded: " + outcome);

      }
      catch (Exception e)
      {
        System.err.println("Unable to read item: " + uuid);
        System.err.println(e.getMessage());
      }
    }
    return product;
  }

  public List<Product> getAllProducts() throws Exception
  {
    ScanSpec scanSpec = new ScanSpec();
    List<Product> products = new ArrayList<>();
    try
    {
      Table table = dynamoDBRepo.getTable(AppConstant.PRODUCT);
      ItemCollection<ScanOutcome> items = table.scan(scanSpec);
      Iterator<Item> iter = items.iterator();
      while (iter.hasNext())
      {
        Item item = iter.next();
        if (item != null)
        {
          Product product = new Product();
          product.setId(item.getString("id"));
          product.setName(item.getString("name"));
          product.setPrice(item.getLong("price"));
          product.setQuantity(item.getInt("quantity"));
          products.add(product);
        }
        System.out.println(item.toString());
      }

    }
    catch (Exception e)
    {
      System.err.println("Unable to scan the table:");
      System.err.println(e.getMessage());
      throw new Exception("Error has been occured while getting list of product");
    }
    return products;
  }

  public void updateProduct(String uuid, String name, Integer quantity)
  {

    UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("id", uuid, "name", name)
        .withUpdateExpression("set quantity = :quantity")
        .withValueMap(new ValueMap().withNumber(":quantity", 5))
        .withReturnValues(ReturnValue.UPDATED_NEW);

    try
    {
      Table table = dynamoDBRepo.getTable(AppConstant.PRODUCT);
      System.out.println("Updating the item...");
      UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
      System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

    }
    catch (Exception e)
    {
      System.err.println("Unable to update item: " + uuid + " " + name);
      System.err.println(e.getMessage());
    }
  }

  public void deleteProduct(String uuid, String name)
  {
    DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
        .withPrimaryKey(new PrimaryKey("id", uuid, "name", name));
    try
    {
      Table table = dynamoDBRepo.getTable(AppConstant.PRODUCT);
      System.out.println("Attempting a conditional delete...");
      table.deleteItem(deleteItemSpec);
      System.out.println("DeleteItem succeeded");
    }
    catch (Exception e)
    {
      System.err.println("Unable to delete item: " + uuid + " " + name);
      System.err.println(e.getMessage());
    }
  }

}
