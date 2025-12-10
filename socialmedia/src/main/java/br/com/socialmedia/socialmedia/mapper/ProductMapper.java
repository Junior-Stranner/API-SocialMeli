package br.com.socialmedia.socialmedia.mapper;

import br.com.socialmedia.socialmedia.dto.request.ProductRequest;
import br.com.socialmedia.socialmedia.dto.response.ProductResponse;
import br.com.socialmedia.socialmedia.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequest dto);

    ProductResponse toDto(Product product);
}
