package com.jonatasrocha.stock.infra.http;

public interface Mapper<I, O> {

    O map(I input);

}
