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
@Table(name = "motherboard_products")
@Getter
@Setter
@NoArgsConstructor
public class MotherboardProduct extends Product {

    private String cpuSocket;
    private String chipset;
    private String formFactor;
    private String memoryType;
    private Integer memorySlotCount;
    private String maxMemoryCapacity;
    private String memorySpeed;
    private Integer pcieX16SlotCount;
    private Integer m2SlotCount;
    private Integer sataPortCount;

    @Override
    public List<SpecItemDto> getSpecs() {
        List<SpecItemDto> list = new ArrayList<>();
        if (getBrand() != null)
            list.add(new SpecItemDto("Hãng sản xuất", getBrand()));
        if (cpuSocket != null)
            list.add(new SpecItemDto("Socket CPU", cpuSocket));
        if (chipset != null)
            list.add(new SpecItemDto("Chipset", chipset));
        if (formFactor != null)
            list.add(new SpecItemDto("Chuẩn bo mạch chủ", formFactor));
        if (memoryType != null)
            list.add(new SpecItemDto("Chuẩn RAM", memoryType));
        if (memorySlotCount != null)
            list.add(new SpecItemDto("Số khe RAM", memorySlotCount + " khe"));
        if (maxMemoryCapacity != null)
            list.add(new SpecItemDto("Dung lượng RAM tối đa", maxMemoryCapacity));
        if (memorySpeed != null)
            list.add(new SpecItemDto("Tốc độ RAM hỗ trợ", memorySpeed));
        if (pcieX16SlotCount != null)
            list.add(new SpecItemDto("Khe PCIe x16", pcieX16SlotCount.toString()));
        if (m2SlotCount != null)
            list.add(new SpecItemDto("Khe M.2", m2SlotCount.toString()));
        if (sataPortCount != null)
            list.add(new SpecItemDto("Cổng SATA", sataPortCount.toString()));
        return list;
    }
}
