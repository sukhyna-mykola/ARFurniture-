package com.sms.arfurniture;

import java.util.ArrayList;
import java.util.List;

public class FurnitureItemHelper {
    private static final FurnitureItemHelper ourInstance = new FurnitureItemHelper();

    public static FurnitureItemHelper getInstance() {
        return ourInstance;
    }

    public List<FurnitureItem> furnitureItems;

    private FurnitureItemHelper() {


        furnitureItems = new ArrayList<>();
        furnitureItems.add(new FurnitureItem(1, FurnitureItem.FurnitureType.CHAIR, "Chair", "Chair description", "chair_model.jpg", "chair_model.sfb"));
        furnitureItems.add(new FurnitureItem(2, FurnitureItem.FurnitureType.SOFA, "Sofa", "Sofa description", "Craft+Sofa.jpg", "Craft+Sofa.sfb"));
        furnitureItems.add(new FurnitureItem(3, FurnitureItem.FurnitureType.TABLE, "Table", "Table description", "table_wood.jpg", "table_wood.sfb"));
        furnitureItems.add(new FurnitureItem(4, FurnitureItem.FurnitureType.TABLE, "Dinner Table", "Dinner Table description", "dinner-table-vikor-3d-model-max-obj-3ds-fbx-stl-dae.jpg", "dinner_table.sfb"));
        furnitureItems.add(new FurnitureItem(5, FurnitureItem.FurnitureType.SHELF, "Shelf", "Shelf  description", "polka.jpg", "polka.sfb"));
        furnitureItems.add(new FurnitureItem(-1, FurnitureItem.FurnitureType.SHELF, "Shelf", "Shelf  description", "plus.png", "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"));

    }


    public List<FurnitureItem> getFurnitureItems() {
        return furnitureItems;
    }

    public FurnitureItem getFurnitireItemById(long id) {
        for (FurnitureItem item : furnitureItems) {
            if (item.getId() == id)
                return item;
        }

        return null;
    }
}
