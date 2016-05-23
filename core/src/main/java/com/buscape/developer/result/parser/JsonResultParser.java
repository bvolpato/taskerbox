/**
 * Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.buscape.developer.result.parser;

import com.buscape.developer.result.type.Address;
import com.buscape.developer.result.type.Addresses;
import com.buscape.developer.result.type.Category;
import com.buscape.developer.result.type.Contact;
import com.buscape.developer.result.type.Contacts;
import com.buscape.developer.result.type.Filter;
import com.buscape.developer.result.type.Filters;
import com.buscape.developer.result.type.ItemList;
import com.buscape.developer.result.type.Link;
import com.buscape.developer.result.type.Links;
import com.buscape.developer.result.type.Offer;
import com.buscape.developer.result.type.Product;
import com.buscape.developer.result.type.Result;
import com.buscape.developer.result.type.Specification;
import com.buscape.developer.util.Messages;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Implementation of AbstractResultParser that parses data in JSON format to Result.
 *
 * @author cartagena
 */
public final class JsonResultParser extends AbstractResultParser {

  private final Gson gson;

  private JsonResultParser(String data) {
    super(data);

    Type listOffer = new TypeToken<List<Offer>>() {}.getType();
    Type listCategory = new TypeToken<List<Category>>() {}.getType();
    Type listProduct = new TypeToken<List<Product>>() {}.getType();
    Type listItem = new TypeToken<List<ItemList>>() {}.getType();

    this.gson =
        new GsonBuilder()
            .setFieldNamingStrategy(new XmlAttributeNamingStrategy())
            .registerTypeAdapter(Addresses.class, new AddressesDeserializer())
            .registerTypeAdapter(Contacts.class, new ContactsDeserializer())
            .registerTypeAdapter(Filters.class, new FiltersDeserializer())
            .registerTypeAdapter(Links.class, new LinksDeserializer())

            .registerTypeAdapter(listCategory, new CategoryListDeserializer())
            .registerTypeAdapter(listItem, new ItemListListDeserializer())
            .registerTypeAdapter(listOffer, new OfferListDeserializer())
            .registerTypeAdapter(listProduct, new ProductListDeserializer())

            .registerTypeAdapter(Specification.class, new SpecificationDeserializer())

            .registerTypeAdapter(XMLGregorianCalendar.class,
                new XMLGregorianCalendarInstanceCreator()).create();
  }

  /**
   * Creates an instance of {@link JsonResultParser} with provided data.
   *
   * @param data the raw data, in JSON, that will be parsed.
   * @return a new instance of {@link JsonResultParser},
   */
  public static AbstractResultParser createInstance(String data) {
    return new JsonResultParser(data);
  }

  @Override
  public Result getResult() {
    if (this.result == null) {
      JsonObject root = new JsonParser().parse(this.data).getAsJsonObject();
      this.result = this.gson.fromJson(root, Result.class);
    }

    return this.result;
  }

  private static class XMLGregorianCalendarInstanceCreator implements
      InstanceCreator<XMLGregorianCalendar> {
    @Override
    public XMLGregorianCalendar createInstance(Type type) {

      try {
        GregorianCalendar gc = new GregorianCalendar();
        DatatypeFactory dtf = DatatypeFactory.newInstance();
        return dtf.newXMLGregorianCalendar(gc);
      } catch (DatatypeConfigurationException e) {
        e.printStackTrace();
      }

      return null;
    }
  }

  private static class AddressesDeserializer implements JsonDeserializer<Addresses> {

    @Override
    public Addresses deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      Addresses result = new Addresses();
      List<Address> list = new ArrayList<>();

      Iterator<JsonElement> it = json.getAsJsonArray().iterator();
      while (it.hasNext()) {
        JsonObject jsonEntry = it.next().getAsJsonObject();
        Address entry =
            context.deserialize(
                jsonEntry.get(Messages.getString("Json.address-key")), Address.class); //$NON-NLS-1$
        list.add(entry);
      }

      result.getAddresses().clear();
      result.getAddresses().addAll(list);

      return result;
    }
  }

  private static class ContactsDeserializer implements JsonDeserializer<Contacts> {

    @Override
    public Contacts deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      Contacts result = new Contacts();
      List<Contact> list = new ArrayList<>();

      Iterator<JsonElement> it = json.getAsJsonArray().iterator();
      while (it.hasNext()) {
        JsonObject jsonEntry = it.next().getAsJsonObject();
        Contact entry =
            context.deserialize(
                jsonEntry.get(Messages.getString("Json.contact-key")), Contact.class); //$NON-NLS-1$
        list.add(entry);
      }

      result.getContacts().clear();
      result.getContacts().addAll(list);

      return result;
    }
  }

  private static class FiltersDeserializer implements JsonDeserializer<Filters> {

    @Override
    public Filters deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      Filters result = new Filters();
      List<Filter> list = new ArrayList<>();

      Iterator<JsonElement> it = json.getAsJsonArray().iterator();
      while (it.hasNext()) {
        JsonObject jsonEntry = it.next().getAsJsonObject();
        Filter entry =
            context
                .deserialize(jsonEntry.get(Messages.getString("Json.contact-key")), Filter.class); //$NON-NLS-1$
        list.add(entry);
      }


      result.getFilters().clear();
      result.getFilters().addAll(list);

      return result;
    }
  }

  private static class LinksDeserializer implements JsonDeserializer<Links> {

    @Override
    public Links deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      Links result = new Links();
      List<Link> list = new ArrayList<>();

      Iterator<JsonElement> it = json.getAsJsonArray().iterator();
      while (it.hasNext()) {
        JsonObject jsonEntry = it.next().getAsJsonObject();
        Link entry =
            context.deserialize(jsonEntry.get(Messages.getString("Json.link-key")), Link.class); //$NON-NLS-1$
        list.add(entry);
      }

      result.getLinks().clear();
      result.getLinks().addAll(list);

      return result;
    }
  }

  private static class CategoryListDeserializer implements JsonDeserializer<List<Category>> {

    @Override
    public List<Category> deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      List<Category> result = new ArrayList<>();

      Iterator<JsonElement> it = json.getAsJsonArray().iterator();
      while (it.hasNext()) {
        JsonObject jsonEntry = it.next().getAsJsonObject();
        String name = jsonEntry.get(Messages.getString("Json.subcategory-key")) != null ? //$NON-NLS-1$
        Messages.getString("Json.subcategory-key") : //$NON-NLS-1$
            Messages.getString("Json.top5category-key"); //$NON-NLS-1$

        Category entry = context.deserialize(jsonEntry.get(name), Category.class);
        result.add(entry);
      }

      return result;
    }
  }

  private static class ItemListListDeserializer implements JsonDeserializer<List<ItemList>> {

    @Override
    public List<ItemList> deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      List<ItemList> result = new ArrayList<>();

      Iterator<JsonElement> it = json.getAsJsonArray().iterator();
      while (it.hasNext()) {
        JsonObject jsonEntry = it.next().getAsJsonObject();
        ItemList entry =
            context.deserialize(jsonEntry.get(Messages.getString("Json.item-key")), ItemList.class); //$NON-NLS-1$
        result.add(entry);
      }

      return result;
    }
  }

  private static class OfferListDeserializer implements JsonDeserializer<List<Offer>> {

    @Override
    public List<Offer> deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      List<Offer> result = new ArrayList<>();

      Iterator<JsonElement> it = json.getAsJsonArray().iterator();
      while (it.hasNext()) {
        JsonObject jsonEntry = it.next().getAsJsonObject();
        Offer entry =
            context.deserialize(jsonEntry.get(Messages.getString("Json.offer-key")), Offer.class); //$NON-NLS-1$
        result.add(entry);
      }

      return result;
    }
  }

  private static class ProductListDeserializer implements JsonDeserializer<List<Product>> {

    @Override
    public List<Product> deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      List<Product> result = new ArrayList<>();

      Iterator<JsonElement> it = json.getAsJsonArray().iterator();
      while (it.hasNext()) {
        JsonObject jsonEntry = it.next().getAsJsonObject();
        Product entry =
            context.deserialize(
                jsonEntry.get(Messages.getString("Json.product-key")), Product.class); //$NON-NLS-1$
        result.add(entry);
      }

      return result;
    }
  }

  private static class SpecificationDeserializer implements JsonDeserializer<Specification> {

    @Override
    public Specification deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      return json.isJsonArray() ? deserializeArray(json, typeOfT, context) : deserializeObject(
          json.getAsJsonObject(), typeOfT, context);
    }

    private Specification deserializeArray(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      Specification result = new Specification();
      List<ItemList> items = result.getItems();

      Type listItem = new TypeToken<List<ItemList>>() {}.getType();

      List<ItemList> deserializedItems = context.deserialize(json, listItem);
      items.addAll(deserializedItems);

      return result;
    }

    private Specification deserializeObject(JsonObject json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      Specification result = new Specification();
      List<ItemList> items = result.getItems();
      Type listItem = new TypeToken<List<ItemList>>() {}.getType();

      if (json.has(Messages.getString("Json.item-key"))) { //$NON-NLS-1$
        List<ItemList> deserializedItems =
            context.deserialize(json.get(Messages.getString("Json.item-key")), listItem); //$NON-NLS-1$
        items.addAll(deserializedItems);
      }

      if (json.has(Messages.getString("Json.links-key"))) { //$NON-NLS-1$
        Links deserializedLinks =
            context.deserialize(json.get(Messages.getString("Json.links-key")), Links.class); //$NON-NLS-1$
        result.setLinks(deserializedLinks);
      }

      if (json.has(Messages.getString("Json.label-key"))) { //$NON-NLS-1$
        result.setLabel(json.get(Messages.getString("Json.label-key")).getAsString()); //$NON-NLS-1$
      }

      return result;
    }

  }

  private static class XmlAttributeNamingStrategy implements FieldNamingStrategy {

    @Override
    public String translateName(Field f) {
      XmlAttribute xmlAttribute = f.getAnnotation(XmlAttribute.class);
      if (xmlAttribute != null) {
        return xmlAttribute.name();
      } else {
        return f.getName();
      }
    }

  }
}
