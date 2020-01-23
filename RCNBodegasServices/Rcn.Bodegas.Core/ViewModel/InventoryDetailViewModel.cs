namespace Rcn.Bodegas.Core.ViewModel
{
    public class InventoryDetailViewModel
    {
        public int InventoryId { get; set; }
        public int ElementId { get; set; }

        public int ResponsibleId { get; set; }

        public string StateDescription { get; set; }

        public int Found { get; set; }

        public string DeliveryDate { get; set; }

        public int ElementType { get; set; }

    }
}
