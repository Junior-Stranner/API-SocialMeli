package br.com.socialmedia.socialmedia.mapper;

import br.com.socialmedia.socialmedia.dto.request.ProductRequest;
import br.com.socialmedia.socialmedia.dto.response.ProductResponse;
import br.com.socialmedia.socialmedia.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public Product toEntity(ProductRequest request) {
        return modelMapper.map(request, Product.class);
    }

    public ProductResponse toDto(Product product) {
        return modelMapper.map(product, ProductResponse.class);
    }

    public List<ProductResponse> toDtoList(List<Product> productuList) {
        return productuList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
