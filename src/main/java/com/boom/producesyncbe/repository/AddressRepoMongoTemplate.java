package com.boom.producesyncbe.repository;
import com.boom.producesyncbe.Data.Address;

import java.util.List;

public interface AddressRepoMongoTemplate {
    List<Address> findSellersNearLocationWithProduct(double longitude, double latitude, double minDistance, double maxDistance, String productNameRegex);
}
