package br.com.socialmedia.socialmedia.controller.User;

import br.com.socialmedia.socialmedia.dto.FollowedListDto;
import br.com.socialmedia.socialmedia.dto.FollowersCountDto;
import br.com.socialmedia.socialmedia.dto.FollowersListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Endpoints de usuários: seguir, deixar de seguir, contagem e listagem de seguidores/seguidos")
public interface UserControllerDocs {

    @Operation(
            summary = "Seguir um usuário (Follow)",
            description = "Cria a relação de follow entre o usuário {userId} e o usuário {userIdToFollow}."
    )
    @ApiResponse(responseCode = "200", description = "Follow realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @PostMapping("/{userId}/follow/{userIdToFollow}")
    ResponseEntity<Void> follow(
            @Parameter(in = ParameterIn.PATH, description = "ID do usuário que irá seguir", example = "1")
            @PathVariable long userId,
            @Parameter(in = ParameterIn.PATH, description = "ID do usuário que será seguido", example = "2")
            @PathVariable long userIdToFollow
    );

    @Operation(
            summary = "Deixar de seguir um usuário (Unfollow)",
            description = "Remove a relação de follow entre o usuário {userId} e o usuário {userIdToUnfollow}."
    )
    @ApiResponse(responseCode = "200", description = "Unfollow realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    ResponseEntity<Void> unfollow(
            @Parameter(in = ParameterIn.PATH, description = "ID do usuário (follower)", example = "1")
            @PathVariable long userId,
            @Parameter(in = ParameterIn.PATH, description = "ID do usuário que será deixado de seguir", example = "2")
            @PathVariable long userIdToUnfollow
    );

    @Operation(
            summary = "Obter quantidade de seguidores",
            description = "Retorna a quantidade de usuários que seguem o usuário informado (normalmente um seller)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Contagem retornada com sucesso",
            content = @Content(schema = @Schema(implementation = FollowersCountDto.class))
    )
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @GetMapping("/{userId}/followers/count")
    ResponseEntity<FollowersCountDto> getFollowersCount(
            @Parameter(in = ParameterIn.PATH, description = "ID do usuário (seller)", example = "2")
            @PathVariable long userId
    );

    @Operation(
            summary = "Listar seguidores (Quem me segue?)",
            description = """
                    Retorna a lista de seguidores do usuário informado.
                    Ordenação suportada (query param 'order'): name_asc | name_desc
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = FollowersListDto.class))
    )
    @ApiResponse(responseCode = "400", description = "Parâmetro 'order' inválido", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @GetMapping("/{userId}/followers/list")
    ResponseEntity<FollowersListDto> getFollowersList(
            @Parameter(in = ParameterIn.PATH, description = "ID do usuário (seller)", example = "2")
            @PathVariable long userId,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Ordenação alfabética: name_asc ou name_desc",
                    example = "name_asc"
            )
            @RequestParam(required = false, defaultValue = "name_asc") String order
    );

    @Operation(
            summary = "Listar seguidos (Quem estou seguindo?)",
            description = """
                    Retorna a lista de usuários seguidos pelo usuário informado.
                    Ordenação suportada (query param 'order'): name_asc | name_desc
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = FollowedListDto.class))
    )
    @ApiResponse(responseCode = "400", description = "Parâmetro 'order' inválido", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @GetMapping("/{userId}/followed/list")
    ResponseEntity<FollowedListDto> getFollowedList(
            @Parameter(in = ParameterIn.PATH, description = "ID do usuário (buyer)", example = "1")
            @PathVariable long userId,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Ordenação alfabética: name_asc ou name_desc",
                    example = "name_desc"
            )
            @RequestParam(required = false, defaultValue = "name_asc") String order
    );
}