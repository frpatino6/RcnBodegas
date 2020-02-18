package com.rcnbodegas.ViewModels;

import androidx.room.Entity;

@Entity
public class InventoryDetailViewModel {
    private String DeliveryDate;
    private int ElementId;
    private String ElementType;
    private int Found;
    private String FoundDate;
    private int InventoryId;
    private int ResponsibleId;
    private String StateDescription;
    private String inventoryUser;

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public int getElementId() {
        return ElementId;
    }

    public void setElementId(int elementId) {
        ElementId = elementId;
    }

    public String getElementType() {
        return ElementType;
    }

    public void setElementType(String elementType) {
        ElementType = elementType;
    }

    public int getFound() {
        return Found;
    }

    public void setFound(int found) {
        Found = found;
    }

    public int getInventoryId() {
        return InventoryId;
    }

    public void setInventoryId(int inventoryId) {
        InventoryId = inventoryId;
    }

    public String getInventoryUser() {
        return inventoryUser;
    }

    public void setInventoryUser(String inventoryUser) {
        this.inventoryUser = inventoryUser;
    }

    public int getResponsibleId() {
        return ResponsibleId;
    }

    public void setResponsibleId(int responsibleId) {
        ResponsibleId = responsibleId;
    }

    public String getStateDescription() {
        return StateDescription;
    }

    public void setStateDescription(String stateDescription) {
        StateDescription = stateDescription;
    }

    public String getFoundDate() {
        return FoundDate;
    }

    public void setFoundDate(String foundDate) {
        FoundDate = foundDate;
    }
}
