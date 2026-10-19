package com.geli.shopinventory.service;

import com.geli.shopinventory.domain.Item;
import com.geli.shopinventory.dto.ItemRequest;
import com.geli.shopinventory.dto.ItemResponse;
import com.geli.shopinventory.dto.ModelMapper;
import com.geli.shopinventory.exception.DuplicateResourceException;
import com.geli.shopinventory.exception.ResourceNotFoundException;
import com.geli.shopinventory.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public ItemResponse createItem(ItemRequest request) {
        if (itemRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Item with code '" + request.code() + "' already exists");
        }

        Item item = Item.builder()
                .code(request.code().trim().toUpperCase())
                .name(request.name().trim())
                .description(request.description())
                .category(request.category())
                .active(request.active() == null || request.active())
                .build();

        Item saved = itemRepository.save(item);
        return ModelMapper.toItemResponse(saved, true);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findWithVariantsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        return ModelMapper.toItemResponse(item, true);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemByCode(String code) {
        Item item = itemRepository.findWithVariantsByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with code: " + code));
        return ModelMapper.toItemResponse(item, true);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems(Boolean activeOnly) {
        List<Item> items = (activeOnly != null && activeOnly)
                ? itemRepository.findByActiveTrue()
                : itemRepository.findAll();

        return items.stream()
                .map(item -> ModelMapper.toItemResponse(item, true))
                .toList();
    }

    @Transactional
    public ItemResponse updateItem(Long id, ItemRequest request) {
        Item item = itemRepository.findWithVariantsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        String newCode = request.code().trim().toUpperCase();
        if (!item.getCode().equalsIgnoreCase(newCode) && itemRepository.existsByCode(newCode)) {
            throw new DuplicateResourceException("Item with code '" + newCode + "' already exists");
        }

        item.setCode(newCode);
        item.setName(request.name().trim());
        item.setDescription(request.description());
        item.setCategory(request.category());
        if (request.active() != null) {
            item.setActive(request.active());
        }

        Item updated = itemRepository.save(item);
        return ModelMapper.toItemResponse(updated, true);
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        itemRepository.delete(item);
    }
}

