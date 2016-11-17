package io.smartcat.data.loader;

import java.util.concurrent.ThreadLocalRandom;

import io.smartcat.data.loader.model.Address;

public class RandomAddressBuilder {

    private String[] cities;
    private String[] streets;
    private long houseNumberStartOfRange;
    private long houseNumberEndOfRange;

    public RandomAddressBuilder randomCityFrom(String... cities) {
        this.cities = cities;
        return this;
    }

    public RandomAddressBuilder randomStreetFrom(String... streets) {
        this.streets = streets;
        return this;
    }

    public RandomAddressBuilder randomHouseNumberRange(long startOfRange, long endOfRange) {
        this.houseNumberStartOfRange = startOfRange;
        this.houseNumberEndOfRange = endOfRange;
        return this;
    }

    public Address build() {
        return buildRandomAddress();
    }

    private Address buildRandomAddress() {
        Address address = new Address();

        int randomCityIndex = ThreadLocalRandom.current().nextInt(0, cities.length);
        address.setCity(cities[randomCityIndex]);

        int randomStreetIndex = ThreadLocalRandom.current().nextInt(0, streets.length);
        address.setStreet(streets[randomStreetIndex]);

        address.setHouseNumber(ThreadLocalRandom.current().nextLong(houseNumberStartOfRange, houseNumberEndOfRange));

        return address;
    }

}
