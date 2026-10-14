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
@Table(name = "cpu_products")
@Getter
@Setter
@NoArgsConstructor
public class CpuProduct extends Product {

    private String series;
    private String generation;
    private String socket;
    private Integer cores;
    private Integer threads;
    private String processingSpeed;
    private String cache;

    @Override
    public List<SpecItemDto> getSpecs() {
        List<SpecItemDto> list = new ArrayList<>();
        if (getBrand() != null)
            list.add(new SpecItemDto("Hãng sản xuất", getBrand()));
        if (series != null)
            list.add(new SpecItemDto("Dòng sản phẩm", series));
        if (generation != null)
            list.add(new SpecItemDto("Thế hệ CPU", generation));
        if (socket != null)
            list.add(new SpecItemDto("Socket", socket));
        if (cores != null)
            list.add(new SpecItemDto("Số nhân", cores + " nhân"));
        if (threads != null)
            list.add(new SpecItemDto("Số luồng", threads + " luồng"));
        if (processingSpeed != null)
            list.add(new SpecItemDto("Tốc độ xử lý", processingSpeed));
        if (cache != null)
            list.add(new SpecItemDto("Cache", cache));
        return list;
    }
}
