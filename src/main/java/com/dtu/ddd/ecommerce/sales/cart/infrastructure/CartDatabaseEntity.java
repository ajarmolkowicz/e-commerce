package com.dtu.ddd.ecommerce.sales.cart.infrastructure;

import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartItem;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartItemId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
public class CartDatabaseEntity {
  private static final Gson gson = new GsonBuilder().registerTypeAdapter(
          new TypeToken<Set<CartItem>>() {
          }.getType(), (JsonDeserializer<Set<CartItem>>) (json, typeOfT, context) -> {
            final Set<CartItem> items = new HashSet<>();
            json.getAsJsonArray().iterator().forEachRemaining($ -> items.add(context.deserialize($, CartItem.class)));
            return items;
          })
      .registerTypeAdapter(CartItem.class,
          (JsonDeserializer<CartItem>) (jsonElement, type, jsonDeserializationContext) -> {
            var obj = jsonElement.getAsJsonObject();
            var id = obj.get("id").getAsJsonObject().get("id").getAsString();
            var productId = obj.get("productId").getAsJsonObject().get("id").getAsString();
            var quantity = obj.get("quantity").getAsJsonObject().get("quantity").getAsInt();
            return new CartItem(
                new CartItemId(UUID.fromString(id)),
                new ProductId(UUID.fromString(productId)),
                new Quantity(quantity)
            );
          }).create();
  @Id
  Long id;
  @Setter UUID cart_id;
  @Setter String items;
  @Setter Integer version;

  public CartDatabaseEntity(UUID id, String items, Integer version) {
    this.cart_id = id;
    this.items = items;
    this.version = version;
  }

  Cart toDomainModel() {
    return new Cart(
        new CartId(cart_id),
        gson.fromJson(items.substring(1, items.length() - 1).replace("\\", ""), new TypeToken<Set<CartItem>>() {}.getType()),
        new Version(version)
    );
  }

  static CartDatabaseEntity fromDomainModel(Cart cart) {
    return new CartDatabaseEntity(
      cart.getId().id(),
        gson.toJson(cart.getItems(), new TypeToken<Set<CartItem>>() {
        }.getType()),
        Optional.ofNullable(cart.getVersion()).map($ -> $.version()).orElse(0)
    );
  }
}
