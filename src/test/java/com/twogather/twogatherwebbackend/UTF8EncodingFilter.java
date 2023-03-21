package com.twogather.twogatherwebbackend;

import io.restassured.filter.Filter;
        import io.restassured.filter.FilterContext;
        import io.restassured.http.ContentType;
        import io.restassured.response.Response;
        import io.restassured.specification.FilterableRequestSpecification;
        import io.restassured.specification.FilterableResponseSpecification;

public class UTF8EncodingFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        requestSpec.contentType(ContentType.JSON.withCharset("UTF-8"));
        responseSpec.contentType(ContentType.JSON.withCharset("UTF-8"));
        return ctx.next(requestSpec, responseSpec);
    }

}