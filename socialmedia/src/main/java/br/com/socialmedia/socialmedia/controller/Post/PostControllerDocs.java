package br.com.socialmedia.socialmedia.controller.Post;

import br.com.socialmedia.socialmedia.dto.request.PostPublishRequest;
import br.com.socialmedia.socialmedia.dto.request.PromoPostPublishRequest;
import br.com.socialmedia.socialmedia.dto.response.FollowedPostsResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoCountResponse;
import br.com.socialmedia.socialmedia.dto.response.PromoPostsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Posts", description = "Endpoints de publicações, feed (últimas 2 semanas) e promoções")
public interface PostControllerDocs {

    @Operation(
            summary = "Publicar um produto (post comum)",
            description = "Cria uma nova publicação de produto para um usuário (seller)."
    )
    @ApiResponse(responseCode = "201", description = "Publicação criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Requisição inválida / validação falhou", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @PostMapping("/products/publish")
    ResponseEntity<Void> publish(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload para publicação do produto",
                    content = @Content(schema = @Schema(implementation = PostPublishRequest.class))
            )
            @RequestBody PostPublishRequest request
    );

    @Operation(
            summary = "Feed: posts dos sellers seguidos nas últimas 2 semanas",
            description = """
                    Retorna as publicações feitas nas últimas duas semanas pelos sellers seguidos pelo usuário informado.
                    Permite ordenação por data via query param 'order': date_asc | date_desc.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Feed retornado com sucesso",
            content = @Content(schema = @Schema(implementation = FollowedPostsResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Parâmetro 'order' inválido", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @GetMapping("/products/followed/{userId}/list")
    ResponseEntity<FollowedPostsResponse> getFollowedPostsLastTwoWeeks(
            @Parameter(in = ParameterIn.PATH, description = "ID do usuário (buyer)", example = "1")
            @PathVariable int userId,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Ordenação por data: date_asc ou date_desc",
                    example = "date_desc"
            )
            @RequestParam(required = false, defaultValue = "date_desc") String order
    );

    @Operation(
            summary = "Publicar um produto em promoção",
            description = "Cria uma publicação com promoção (ex.: desconto/flag de promoção) para um usuário (seller)."
    )
    @ApiResponse(responseCode = "201", description = "Promo criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Requisição inválida / validação falhou", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @PostMapping("/products/promo-post")
    ResponseEntity<Void> publishPromo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload para publicação em promoção",
                    content = @Content(schema = @Schema(implementation = PromoPostPublishRequest.class))
            )
            @RequestBody PromoPostPublishRequest request
    );

    @Operation(
            summary = "Quantidade de publicações em promoção de um seller",
            description = "Retorna a quantidade de posts/produtos em promoção para o seller informado."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Quantidade retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PromoCountResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @GetMapping("/products/promo-post/count")
    ResponseEntity<PromoCountResponse> getPromoCount(
            @Parameter(in = ParameterIn.QUERY, description = "ID do seller", example = "2")
            @RequestParam int userId
    );

    @Operation(
            summary = "Listar publicações em promoção de um seller (EXTRA/Bônus)",
            description = "Retorna a lista de publicações em promoção do seller informado."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PromoPostsResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @GetMapping("/products/promo-post/list")
    ResponseEntity<PromoPostsResponse> getPromoPosts(
            @Parameter(in = ParameterIn.QUERY, description = "ID do seller", example = "2")
            @RequestParam int sellerId,
            @RequestParam int buyerId
    );
}