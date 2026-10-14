package com.reforgepc.entity;

import com.reforgepc.dto.SpecItemDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cooler_products")
@Getter
@Setter
@NoArgsConstructor
public class CoolerProduct extends Product {

    private String coolerType;
    private String supportedSockets;
    private String height;
    private String tdpRating;

    @Override
    public List<SpecItemDto> getSpecs() {
        List<SpecItemDto> list = new ArrayList<>();
        if (getBrand() != null) list.add(new SpecItemDto("Hãng sản xuất", getBrand()));
        if (coolerType != null) list.add(new SpecItemDto("Loại tản nhiệt", coolerType));
        if (supportedSockets != null) list.add(new SpecItemDto("Socket hỗ trợ", supportedSockets));
        if (height != null) list.add(new SpecItemDto("Chiều cao", height));
        if (tdpRating != null) list.add(new SpecItemDto("Công suất thiết kế nhiệt", tdpRating));
        return list;
    }
}
