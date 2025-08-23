package com.example.ofek_recipes.controllers;

import com.example.ofek_recipes.dtos.RecipeResponse;
import com.example.ofek_recipes.entities.AppUser;
import com.example.ofek_recipes.entities.Recipe;
import com.example.ofek_recipes.entities.RecipeTag;
import com.example.ofek_recipes.repositories.RecipesRepository;
import com.example.ofek_recipes.repositories.UsersRepository;
import com.example.ofek_recipes.services.FileStorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipesController {
    private final RecipesRepository recipesRepository;
    private final UsersRepository usersRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public Page<RecipeResponse> listRecipes(@PageableDefault(size = 10)Pageable page) {
        return recipesRepository.findAll(page).map(this::recipeToResponse);
    }

    @GetMapping("/{id}")
    public RecipeResponse getRecipeById(@PathVariable("id") UUID id) {
        return recipeToResponse(recipesRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse createRecipe(Authentication authentication,
                                       @RequestPart("title") String title,
                                       @RequestPart("ingredientMd") String ingredientMd,
                                       @RequestPart("processMd") String processMd,
                                       @RequestPart(value = "description", required = false) String description,
                                       @RequestPart(name = "prepTimeMinutes", required = false) String prepTimeMinutes,
                                       @RequestPart(name = "tags", required = false) String tagsString,
                                       @RequestPart(name = "photo", required = false) MultipartFile photo,
                                       @RequestPart(name = "photoUrl", required = false) String photoUrl) throws IOException {
        AppUser author = usersRepository.findByEmail(authentication.getName()).orElseThrow();
        String imgUrl = (photo != null && !photo.isEmpty())
                ? fileStorageService.save(photo)
                : photoUrl;

        List<RecipeTag> tags = tagsString != null ? objectMapper.readValue(tagsString, new com.fasterxml.jackson.core.type.TypeReference<List<RecipeTag>>() {}) : null;
        Recipe recipe = Recipe.builder()
                .title(title)
                .description(description)
                .processMd(processMd)
                .ingredientMd(ingredientMd)
                .userId(author.getId())
                .photoUrl(imgUrl)
                .prepTimeMinutes(Integer.parseInt(prepTimeMinutes != null ? prepTimeMinutes : "0"))
                .tagsJson(objectMapper.valueToTree(tags))
                .build();
        return recipeToResponse(recipesRepository.save(recipe));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RecipeResponse updateRecipeById(Authentication authentication,
                                           @PathVariable UUID id,
                                           @RequestPart("title") String title,
                                           @RequestPart(value = "description", required = false) String description,
                                           @RequestPart("ingredientMd") String ingredientMd,
                                           @RequestPart("processMd") String processMd,
                                           @RequestPart(name = "prepTimeMinutes", required = false) String prepTimeMinutes,
                                           @RequestPart(name = "tags", required = false) String tagsString,
                                           @RequestPart(name = "photoUrl", required = false) String photoUrl,
                                           @RequestPart(name = "photo", required = false) MultipartFile photo) throws IOException {
        Recipe recipe = recipesRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AppUser user = usersRepository.findByEmail(authentication.getName()).orElseThrow();
        if (!recipe.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own recipes");
        }
        if (title != null && !title.isEmpty()) {
            recipe.setTitle(title);
        }
        recipe.setDescription(description);
        recipe.setIngredientMd(ingredientMd);
        recipe.setProcessMd(processMd);
        recipe.setPrepTimeMinutes(Integer.parseInt(prepTimeMinutes != null ? prepTimeMinutes : "0"));

        if (tagsString != null) {
            List<RecipeTag> tags = objectMapper.readValue(tagsString, new com.fasterxml.jackson.core.type.TypeReference<List<RecipeTag>>() {});
            recipe.setTagsJson(objectMapper.valueToTree(tags));
        }
        if (photo != null) {
            recipe.setPhotoUrl(fileStorageService.save(photo));
        } else {
            recipe.setPhotoUrl(photoUrl);
        }
        return recipeToResponse(recipesRepository.save(recipe));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipeById(Authentication authentication, @PathVariable UUID id) {
        Recipe recipe = recipesRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AppUser user = usersRepository.findByEmail(authentication.getName()).orElseThrow();
        if (!recipe.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own recipes");
        }
        recipesRepository.deleteById(id);
    }

    @GetMapping("/photo/{filename}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String filename) throws IOException {
        Path uploadsDir = Paths.get("uploads");
        Path filePath = uploadsDir.resolve(filename).normalize();
        if (!Files.exists(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }
        String contentType = Files.probeContentType(filePath);
        byte[] fileBytes = Files.readAllBytes(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(fileBytes);
    }

    private RecipeResponse recipeToResponse(Recipe recipe) {
        AppUser user = usersRepository.findById(recipe.getUserId()).orElseThrow();
        return new RecipeResponse(recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getIngredientMd(),
                recipe.getProcessMd(),
                recipe.getPhotoUrl(),
                recipe.getUserId(),
                user.getName(),
                recipe.getCreatedAt(),
                recipe.getUpdatedAt(),
                recipe.getPrepTimeMinutes(),
                objectMapper.convertValue(recipe.getTagsJson(), new TypeReference<>() {}));
    }
}
