package com.rcnbodegas.ViewModels;

public class InventoryDetailViewModel {
    private int InventoryId ;
    private int ElementId ;
    private int ResponsibleId ;
    private String StateDescription ;
    private int Found ;
    private String DeliveryDate ;
    private String ElementType ;

    public int getInventoryId() {
        return InventoryId;
    }

    public void setInventoryId(int inventoryId) {
        InventoryId = inventoryId;
    }

    public int getElementId() {
        return ElementId;
    }

    public void setElementId(int elementId) {
        ElementId = elementId;
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

    public int getFound() {
        return Found;
    }

    public void setFound(int found) {
        Found = found;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public String getElementType() {
        return ElementType;
    }

    public void setElementType(String elementType) {
        ElementType = elementType;
    }
}
