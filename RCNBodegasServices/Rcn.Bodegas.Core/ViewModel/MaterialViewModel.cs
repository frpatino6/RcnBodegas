using System.Collections.Generic;

namespace Rcn.Bodegas.Core.ViewModel
{
  public class MaterialViewModel
  {
    public int Id { get; set; }

    public string wareHouseId { get; set; }
    public string wareHouseName { get; set; }
    public int productionId { get; set; }
    public string productionName { get; set; }
    public int responsibleId { get; set; }
    public string responsibleName { get; set; }
    public int typeElementId { get; set; }
    public string typeElementName { get; set; }
    public string marca { get; set; }
    public string barCode { get; set; }
    public string materialName { get; set; }
    public decimal unitPrice { get; set; }
    public decimal purchaseValue { get; set; }
    public string userCreated { get; set; }
    public bool isReview { get; set; }
    public bool isAdmin { get; set; }
    public List<string> ListaImagenesStr { get; set; }
    public string legalizedBy { get; set; }
    public System.DateTime saleDate { get; set; }

    public string terceroActual { get; set; }
  }
}
