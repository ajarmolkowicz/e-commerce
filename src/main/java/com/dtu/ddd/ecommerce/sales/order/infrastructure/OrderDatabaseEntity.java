package com.dtu.ddd.ecommerce.sales.order.infrastructure;

import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.order.domain.ShippingTime;
import com.dtu.ddd.ecommerce.sales.order.domain.SubmissionTime;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;


@NoArgsConstructor
public class OrderDatabaseEntity {
  private static final Gson gson = new GsonBuilder().registerTypeAdapter(
      new TypeToken<Set<OrderItem>>() {
      }.getType(), (JsonDeserializer<Set<OrderItem>>) (json, typeOfT, context) -> {
        final Set<OrderItem> items = new HashSet<>();
        json.getAsJsonArray().iterator().forEachRemaining($ -> items.add(context.deserialize($, OrderItem.class)));
        return items;
      })
      .registerTypeAdapter(OrderItem.class,
          (JsonDeserializer<OrderItem>) (jsonElement, type, jsonDeserializationContext) -> {
            var obj = jsonElement.getAsJsonObject();
            var id = obj.get("productId").getAsJsonObject().get("id").getAsString();
            var money = obj.get("money").getAsJsonObject().get("money").getAsJsonObject();
            var quantity = obj.get("quantity").getAsJsonObject().get("quantity").getAsInt();
            return new OrderItem(
                new ProductId(UUID.fromString(id)),
                    Money.of(CurrencyUnit.of(money.get("currency").getAsJsonObject().get("code").getAsString()), money.get("amount").getAsBigDecimal()),
                    new Quantity(quantity));
          }).create();
  @Setter UUID id;
  @Setter String items;
  @Setter String total;
  @Setter String submission_time;
  @Setter String shipping_time;
  @Setter Integer version;

  Order toDomainModel() {
    return new Order(
        new OrderId(id),
        gson.fromJson(items.substring(1, items.length() - 1).replace("\\", ""), new TypeToken<Set<OrderItem>>() {}.getType()),
        Money.parse(total),
        new SubmissionTime(ZonedDateTime.parse(submission_time)),
        Optional.ofNullable(shipping_time).map($ -> new ShippingTime(ZonedDateTime.parse($))).orElse(null),
        new Version(version)
    );
  }
}
